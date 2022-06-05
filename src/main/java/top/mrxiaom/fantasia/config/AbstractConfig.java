package top.mrxiaom.fantasia.config;

import com.google.common.collect.Lists;
import com.google.gson.*;
import top.mrxiaom.fantasia.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解驱动的配置文件系统
 *
 * @author MrXiaoM
 */
public abstract class AbstractConfig {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Config {
        String value();
    }

    public static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
    public final File configFile;
    private JsonObject defaultConfig;
    private final Map<String, Field> configPath = new HashMap<>();

    public AbstractConfig(File configFile) {
        this.configFile = configFile;
        this.initDefaultConfig();
    }

    public final JsonObject getDefaultConfig() {
        return defaultConfig;
    }

    public final void initDefaultConfig() {
        try {
            configPath.clear();
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Config annConfig = f.getDeclaredAnnotation(Config.class);
                if (annConfig == null) continue;
                configPath.put(annConfig.value(), f);
            }
            defaultConfig = saveAsJson();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            if (configFile.exists()) {
                JsonElement jsonElement = new JsonParser().parse(Utils.readAsString(configFile));
                JsonObject json = jsonElement.getAsJsonObject();
                loadFromJson(json);
            }
            saveConfig();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public final void loadDefault() {
        loadFromJson(defaultConfig);
    }

    public final void loadFromJson(JsonObject json) {
        for (String key : configPath.keySet()) {
            List<String> path = Lists.newArrayList(key.contains(".") ? key.split("\\.") : new String[]{key});
            read(json, path, configPath.get(key));
        }
    }

    public final JsonObject saveAsJson() {
        JsonObject json = new JsonObject();
        for (String str : configPath.keySet()) {
            try {
                Field f = configPath.get(str);
                JsonElement value = toJsonType(f.get(this));
                if (value == null) continue;
                List<String> path = Lists.newArrayList(str.contains(".") ? str.split("\\.") : new String[]{str});
                write(json, path, value);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return json;
    }

    public void saveConfig() {
        Utils.saveFromString(configFile, PRETTY.toJson(saveAsJson()));
    }

    private void read(JsonObject json, List<String> path, Field field) {
        if (path.size() == 1) {
            Object value = toRawType(json.get(path.get(0)));
            if (value == null) return;
            try {
                // json 里拿到的 Number 是 com.google.gson.internal.LazilyParsedNumber
                // 无法直接强制转换为 java 自带类型，因此需要额外操作来拆箱
                if (value instanceof Number) {
                    Number num = (Number) value;
                    if (classContains(field.getType(), int.class, Integer.class)) value = num.intValue();
                    else if (classContains(field.getType(), long.class, Long.class)) value = num.longValue();
                    else if (classContains(field.getType(), float.class, Float.class)) value = num.floatValue();
                    else if (classContains(field.getType(), double.class, Double.class)) value = num.longValue();
                    else if (classContains(field.getType(), short.class, Short.class)) value = num.shortValue();
                    else if (classContains(field.getType(), byte.class, Byte.class)) value = num.byteValue();
                    else return;
                }
                field.set(this, value);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
        String key = path.get(0);
        path.remove(0);
        read(json.getAsJsonObject(key), path, field);
    }

    private void write(JsonObject json, List<String> path, JsonElement value) {
        if (path.size() == 1) {
            json.add(path.get(0), value);
            return;
        }
        String key = path.get(0);
        path.remove(0);

        JsonObject child = json.has(key) ? json.getAsJsonObject(key) : new JsonObject();
        write(child, path, value);

        if (json.has(key)) json.remove(key);
        json.add(key, child);
    }

    @Nullable
    public static Object toRawType(JsonElement json) {
        if (json == null) return null;
        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            // 坑: 此处 Number 为 com.google.gson.internal.LazilyParsedNumber
            if (primitive.isNumber()) return primitive.getAsNumber();
            if (primitive.isString()) return primitive.getAsString();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
            else try {
                Field f = JsonPrimitive.class.getDeclaredField("value");
                f.setAccessible(true);
                return f.get(primitive);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (json.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            JsonArray array = json.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                Object value = toRawType(array.get(i));
                list.add(value);
            }
            return list;
        }
        if (json.isJsonObject()) {
            Map<String, Object> map = new HashMap<>();
            JsonObject object = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                Object value = toRawType(entry.getValue());
                map.put(entry.getKey(), value);
            }
            return map;
        }

        return null;
    }

    @Nullable
    public static JsonElement toJsonType(Object obj) {
        if (obj instanceof Number) return new JsonPrimitive((Number) obj);
        if (obj instanceof String) return new JsonPrimitive((String) obj);
        if (obj instanceof Boolean) return new JsonPrimitive((Boolean) obj);
        if (obj instanceof Character) return new JsonPrimitive((Character) obj);
        if (obj instanceof List) {
            JsonArray json = new JsonArray();
            List<?> list = (List<?>) obj;
            for (Object o : list) {
                JsonElement value = toJsonType(o);
                if (value != null) json.add(value);
            }
            return json;
        }
        if (obj instanceof Map) {
            JsonObject json = new JsonObject();
            Map<?, ?> map = (Map<?, ?>) obj;
            for (Object key : map.keySet()) {
                if (!(key instanceof String)) continue;
                JsonElement value = toJsonType(map.get(key));
                if (value != null) json.add((String) key, value);
            }
            return json;
        }
        return null;
    }

    private static boolean classContains(Class<?> cls, Class<?>... compare) {
        for (Class<?> c : compare) {
            if (cls.equals(c)) return true;
        }
        return false;
    }
}
