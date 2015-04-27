package org.fenixedu.bennu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import pt.ist.fenixframework.DomainObject;

import com.fatboyindustrial.gsonjodatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BeanConverterService implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.class.equals(sourceType.getType()) && IBean.class.isAssignableFrom(targetType.getType());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, IBean.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        JsonParser parser = new JsonParser();

        String jsonStr = (String) source;

        JsonElement parse = parser.parse((String) jsonStr);

        if (parse instanceof JsonArray) {
            ArrayList list = new ArrayList();
            JsonArray jsonArray = (JsonArray) parse;
            for (int i = 0; i < jsonArray.size(); i++) {
                String className = jsonArray.get(i).getAsJsonObject().get("classname").getAsString();
                list.add(convertObject(jsonArray.get(i), targetType));
            }
            return list;
        } else {
            return convertObject(parse, targetType);
        }
    }

    private Object convertObject(JsonElement jsonElement, TypeDescriptor targetType) {
        String className = jsonElement.getAsJsonObject().get("classname").getAsString();
        Class beanObjectClass;
        try {
            beanObjectClass = Class.forName(className);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(LocalizedString.class, new LocalizedStringAdapter());
            builder.registerTypeHierarchyAdapter(DomainObject.class, new DomainObjectAdapter());
            Gson gson = Converters.registerAll(builder).create();
            Object beanObject = gson.fromJson(jsonElement, beanObjectClass);
            //throws ClassCastException if not required domain type
            return targetType.getType().cast(beanObject);
            
        } catch (final Exception e) {
            System.out.print("Error deserializing bean :" + e.getMessage());
            System.out.print("Bean JSON :" + jsonElement.toString());
            
            throw new RuntimeException(e);
        }
    }

}
