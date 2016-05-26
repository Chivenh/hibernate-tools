/*
 * Created on 2004-12-01
 */
package org.hibernate.tool.hbm2x;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.tool.hbm2x.pojo.ComponentPOJOClass;
import org.hibernate.tool.hbm2x.pojo.POJOClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author max and david
 */
public class ConfigurationNavigator {

	private static final Logger log = LoggerFactory.getLogger(POJOExporter.class);
	
	/**
	 * @param cfg
	 * @param exporter
	 * @param file
	 */
	public void export(Configuration cfg, ConfigurationVisitor exporter) {

		Map<String, Component> components = new HashMap<String, Component>();
		Metadata md = getMetadata(cfg);

		for (Iterator<PersistentClass> classes = md.getEntityBindings().iterator(); classes.hasNext(); ) {
		    if(exporter.startMapping(cfg) ) {
		        PersistentClass clazz = classes.next();
		        collectComponents(components,clazz);
		        
		        if(exporter.startPersistentClass(clazz) ) {
		            if(clazz.hasIdentifierProperty() ) {
		                exporter.startIdentifierProperty(clazz.getIdentifierProperty() );
		                exporter.endIdentifierProperty(clazz.getIdentifierProperty() );
		            } 
		            else if (clazz.hasEmbeddedIdentifier() ) {
						exporter.startEmbeddedIdentifier( (Component)clazz.getKey() );
						exporter.endEmbeddedIdentifier( (Component)clazz.getKey() );
		            }
		            Iterator<?> unjoinedPropertyIterator = clazz.getUnjoinedPropertyIterator();
		            while(unjoinedPropertyIterator.hasNext() ) {
		                Property prop = (Property)unjoinedPropertyIterator.next();
		                exporter.startProperty(prop);
		                exporter.endProperty(prop);
		            }
		        } 
		        exporter.endPersistentClass(clazz);
		    } 
		    else {
		        exporter.endMapping(cfg);
		    }
		}
		
		for(Iterator<?> comps = components.values().iterator(); comps.hasNext(); ) {
			Component component = (Component)comps.next();
			exporter.startComponent(component);
		}
		
		if (exporter.startGeneralConfiguration(cfg) )
			exporter.endGeneralConfiguration(cfg);

	}

	/**
	 * @param clazz
	 */
	public static void collectComponents(Map<String, Component> components, PersistentClass clazz) {
		Iterator<Property> iter = new Cfg2JavaTool().getPOJOClass(clazz).getAllPropertiesIterator();
		collectComponents( components, iter );		
	}

	public static void collectComponents(Map<String, Component> components, POJOClass clazz) {
		Iterator<Property> iter = clazz.getAllPropertiesIterator();
		collectComponents( components, iter );		
	}
	
	private static void collectComponents(Map<String, Component> components, Iterator<Property> iter) {
		while(iter.hasNext()) {
			Property property = iter.next();
			if (!"embedded".equals(property.getPropertyAccessorName()) && // HBX-267, embedded property for <properties> should not be generated as component. 
				property.getValue() instanceof Component) {
				Component comp = (Component) property.getValue();
				addComponent( components, comp );			
			} 
			else if (property.getValue() instanceof Collection) {
				// compisite-element in collection
				Collection collection = (Collection) property.getValue();				
				if ( collection.getElement() instanceof Component) {
					Component comp = (Component) collection.getElement();				
					addComponent(components, comp);				
				}
			}
		}
	}

	private static void addComponent(Map<String, Component> components, Component comp) {
		if(!comp.isDynamic()) {
			Component existing = (Component) components.put(
					comp.getComponentClassName(), 
					comp);		
			if(existing!=null) {
				log.warn("Component " + existing.getComponentClassName() + " found more than once! Will only generate the last found.");
			}
		} else {
			log.debug("dynamic-component found. Ignoring it as a component, but will collect any embedded components.");
		}	
		collectComponents( 
				components, 
				new ComponentPOJOClass(comp, new Cfg2JavaTool()).getAllPropertiesIterator());		
	}
	
	private Metadata getMetadata(Configuration configuration) {
		Metadata result = null;
		try {
			Field metadataSourcesField = 
					Configuration.class.getDeclaredField("metadataSources");
			metadataSourcesField.setAccessible(true);
			MetadataSources metadataSources = 
					(MetadataSources) metadataSourcesField.get(configuration);
			result = metadataSources.buildMetadata();
		} catch (NoSuchFieldException | 
				SecurityException | 
				IllegalArgumentException | 
				IllegalAccessException e) {
			// This should in principle never happen, 
			// maybe only some day when the metadataSources field 
			// is removed from the Configuration class.
			throw new RuntimeException(e);
		}
		return result;
	}

}
