package com.dewey.rpc.common.serializer.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.log4j.Logger;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工具
 * @author dewey
 * @date 2018/9/8 14:13
 */
public class SerializationUtil {

    private static Logger logger = Logger.getLogger(SerializationUtil.class);

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    /**
     * 获取序列化模板
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if(schema==null){
            schema = RuntimeSchema.createFrom(clazz);
            if(schema!=null){
                cachedSchema.put(clazz,schema);
            }
        }
        return schema;
    }

    /**
     * 序列化  message to byte[]
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj){
        Class<T> clazz = (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }catch (Exception e){
            logger.info("序列化失败",e);
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

    /**
     * 反序列化  byte[] to message
     * @param data
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data,Class<T> obj){
        try{
            T message = objenesis.newInstance(obj);
            Schema<T> schema = getSchema(obj);
            ProtostuffIOUtil.mergeFrom(data,message,schema);
            return message;
        }catch (Exception e){
            logger.info("反序列化失败",e);
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

}
