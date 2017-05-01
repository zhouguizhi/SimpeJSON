package com.simple;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Created by Adminis on 2017/4/30.
 */
public class SimpleJSON {
    private static final String TAG = SimpleJSON.class.getSimpleName();
    private SimpleJSON(){}
    /**
     * 如果json最外层直接是数组的话
     * @param json
     * @param clazz
     * @return
     */
    public static List<?> jsonToList(String json, final  Class clazz){
        List<Object> list = null;
        try {
            JSONArray jsonArray =  new JSONArray(json);
            list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String jsonArrayValue = jsonArray.getJSONObject(i).toString();
                switch (getType(jsonArrayValue)) {
                    case OBJECT: {
                        list.add(toObj(jsonArrayValue, clazz));
                        break;
                    }
                    case ARRAY: {
                        List<?> infoList = toList(jsonArrayValue, clazz);
                        list.add(infoList);
                        break;
                    }
                    default:
                        break;
                }
            }
        } catch (Exception e) {
        }
        return list;
    }
    /**
     * json转model
     * @return
     */
    public static Object toObj(String json,final  Class clazz){
        if(!TextUtils.isEmpty(json)&&clazz!=null){
            Object object = null;
            try {
                if (json.charAt(0) == '[') { //数组
                    object = toList(json, clazz);
                } else if (json.charAt(0) == '{'){  //对象
                    JSONObject infoObject = new JSONObject(json);
                    //反射获取最外层对象 （注意 一定要有空的构造函数）
                    object = clazz.newInstance();
                    Iterator<?> iterator = infoObject.keys();
                    while (iterator!=null&&iterator.hasNext()) {
                        String key = (String) iterator.next();
                        Object fieldValue = null;
                        List<Field> fields = getAllFields(clazz, null);
                        for (int i = 0; i < fields.size(); i++) {
                            Field field = fields.get(i);
                            if(!TextUtils.isEmpty(field.getName())){
                                if (field.getName().equalsIgnoreCase(key)) {
                                    field.setAccessible(true);
                                    fieldValue = getFieldValue(field, infoObject, key);
                                    if (null != fieldValue) {
                                        field.set(object, fieldValue);
                                    }
                                    field.setAccessible(false);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }
            return object;
        }
        return  null;
    }
    private static Object getFieldValue(Field field, JSONObject infoObject, String key) throws Exception {
        Object fieldValue = null;
        Class<?> fieldClass = field.getType();//得到当前成员变量的类型
        if("int".equals(fieldClass.getSimpleName().toString())||"Integer".equals(fieldClass.getSimpleName().toString())){
            fieldValue = infoObject.getInt(key);
        }else if("String".equals(fieldClass.getSimpleName().toString())){
            fieldValue = infoObject.getString(key);
        }else if(fieldClass.getSimpleName().toString().equalsIgnoreCase("double")){
            fieldValue = infoObject.getDouble(key);
        }else if(fieldClass.getSimpleName().toString().equalsIgnoreCase("boolean")){
            fieldValue = infoObject.getBoolean(key);
        } else if (fieldClass.getSimpleName().toString().equalsIgnoreCase("long")) {
            fieldValue = infoObject.getLong(key);
        } else {//对象类型
            String jsonValue = infoObject.getString(key);
            switch (getType(jsonValue)) {
                case OBJECT: {
                    fieldValue = toObj(jsonValue,fieldClass);
                    break;
                }
                case ARRAY: {
                    // 获取泛型的class
                    Type genericFieldType = field.getGenericType();
                    if (genericFieldType instanceof ParameterizedType) {
                        ParameterizedType aType = (ParameterizedType) genericFieldType;
                        Type[] fieldArgTypes = aType.getActualTypeArguments();
                        for (Type fieldArgType : fieldArgTypes) {
                            Class<?> fieldArgClass = (Class<?>) fieldArgType;
                            fieldValue = toList(jsonValue, fieldArgClass);
                        }
                    }
                    break;
                }
                default://json类型错误
                    break;
            }
        }
        return fieldValue;
    }
    private static JSON_TYPE getType(String str) {
        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];
        if (firstChar == '{') {
            return JSON_TYPE.OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE.ARRAY;
        }
        return JSON_TYPE.ERROR;
    }
    private enum JSON_TYPE {
        OBJECT,
        ARRAY,
        ERROR
    }
    /**
     * 解析JsonArray数组
     * @param json
     * @param clazz
     * @return
     */
    private static List<?> toList(String json, Class clazz) {
        List<Object> list = null;
        try {
            JSONArray jsonArray = (JSONArray) new JSONArray(json);
            list = new ArrayList<Object>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String jsonvalue = jsonArray.getJSONObject(i).toString();
                switch (getType(jsonvalue)) {
                    case OBJECT: {
                        list.add(toObj(jsonvalue, clazz));
                        break;
                    }
                    case ARRAY: {
                        List<?> infoList = toList(jsonvalue, clazz);
                        list.add(infoList);
                        break;
                    }
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return list;
    }

    /**
     * model 转json
     * @param obj
     * @return
     */
    public static String toJson(Object obj){
        String json = "";
        if(obj!=null){
            StringBuffer sb = new StringBuffer();
            if(obj instanceof List){
                sb.append("[");
                List<?> list= (List<?>) obj;
                for (int i=0;i<list.size();i++) {
                    parseObjToJson(sb, list.get(i));
                    if (i < list.size() - 1)
                    {
                        sb.append(",");
                    }
                }
                sb.append("]");
            }else{
                parseObjToJson(sb, obj);
            }
            json = sb.toString();
        }
        return json;
    }
    private static void parseObjToJson(StringBuffer sb, Object obj){
        if(sb!=null&&obj!=null){
            sb.append("{");
            List<Field> fields=new ArrayList<>();
             getAllFields(obj.getClass(),fields);
            if(!fields.isEmpty()){
                for(int i=0;i<fields.size();i++){
                    Method method=null;
                    Field field=fields.get(i);
                    Object  fieldValue=null;
                    //$change 要排除这个字段
                    String fieldName=field.getName();
                    String methodName = "";
                    if(field.getType()==boolean.class||field.getType()==Boolean.class){
                        if(!TextUtils.isEmpty(fieldName)&&fieldName.startsWith("is")){
                            methodName=fieldName;
                        }else{
                            methodName="is"+((char)(fieldName.charAt(0)-32)+fieldName.substring(1));
                        }
                    }else{
                        methodName="get"+((char)(fieldName.charAt(0)-32)+fieldName.substring(1));
                    }
                    try {
                        method=obj.getClass().getMethod(methodName);
                    } catch (NoSuchMethodException e) {
                    }
                    if(method!=null)
                    {
                        try {
                            fieldValue =method.invoke(obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    if(fieldValue!=null)
                    {
                        sb.append("\"");
                        sb.append(fieldName);
                        sb.append("\":");
                        if(fieldValue instanceof Integer
                                || fieldValue  instanceof  Double||
                                fieldValue instanceof Long||
                                fieldValue instanceof Boolean)
                        {
                            sb.append(fieldValue.toString());
                        }else if(fieldValue instanceof String)
                        {
                            sb.append("\"");
                            sb.append(fieldValue.toString());
                            sb.append("\"");
                        }else if(fieldValue instanceof  List)
                        {
                            parseListToJson(sb,fieldValue);
                        }else
                        {
                            parseObjToJson(sb,fieldValue);
                        }
                        if(i!=(fields.size()-1)){//排除最后一个字段加逗号
                            sb.append(",");
                        }
                    }
                }
            }
            sb.append("}");
        }
    }
    private static void parseListToJson(StringBuffer sb, Object fieldValue) {
        if(sb!=null&&fieldValue!=null){
            List list= (List) fieldValue;
            sb.append("[");
            for (int i=0;i<list.size();i++)
            {
                parseObjToJson(sb,list.get(i));
                if(i!=(list.size()-1))
                {
                    sb.append(",");
                }
            }
            sb.append("]");
        }
    }
    private static  List<Field> getAllFields(Class<?> clazz, List<Field> fields) {
        if(clazz==null){
            return null;
        }
        if(fields==null)
        {
            fields=new ArrayList<>();
        }
        //获取所有的声明字段
        Field[] declaredFields=clazz.getDeclaredFields();
        for(Field field:declaredFields)
        {
            if(!Modifier.isFinal(field.getModifiers())&&!"$change".equals(field.getName()))//不是final修饰的 并且排除$change
            {
                fields.add(field);
            }
        }
        return fields;
    }
}
