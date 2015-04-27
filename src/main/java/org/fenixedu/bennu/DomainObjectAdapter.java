
package org.fenixedu.bennu;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

/***
 * Converts a string to a DomainObject
 * 
 */
public class DomainObjectAdapter extends TypeAdapter<DomainObject> implements InstanceCreator {
	

	 
	

	@Override
	public DomainObject read(JsonReader reader) throws IOException {
		final DomainObject domainObject = FenixFramework.getDomainObject(reader.nextString());


        if (FenixFramework.isDomainObjectValid(domainObject)) {
            return domainObject;
        }

        return null;
	}


	@Override
	public void write(JsonWriter writer, DomainObject src) throws IOException {
		 writer.value(src != null ? src.getExternalId() : "");
	}


	@Override
	public Object createInstance(Type arg0) {
		return null;
	}
}
