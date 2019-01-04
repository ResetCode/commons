/**
 * 
 */
package com.using.common.util;

import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * Created by liumohan 2018-06-28 16:41
 */
public class XmlUtils {

	private static XmlMapper xmlMapper = new XmlMapper();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String xml2Json(String xml) throws Exception {
		
		StringWriter writer = new StringWriter();
		try {
			JsonParser jp = xmlMapper.getFactory().createParser(xml);
			JsonGenerator jg = objectMapper.getFactory().createGenerator(writer);
			while(jp.nextToken() != null) {
				jg.copyCurrentEvent(jp);
			}
			jp.close();
			jg.close();
		} catch (Exception e) {
			throw e;
		}
		
		return writer.toString();
	}
	
}
