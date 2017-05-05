 package info.kapable.utils.txttomail.htmlprocessor;
 
 import info.kapable.utils.txttomail.exception.TemplateProcessingException;

 import java.util.Map;
 import java.util.Properties;
 
 /**
  * Return a html processor by tag
  * @author MGOULIN
  *
  */
 public class HTMLProcessorBuilder
 {
   public static HTMLProcessor getTemplateProcessor(String string, Properties config, Map<String, String> headers)
     throws TemplateProcessingException
   {
     if (config.getProperty(string + ".html.template") != null) {
       return new FreemarkerHTMLProcessor(config.getProperty(string + ".html.template"), headers);
     }
     return new FreemarkerHTMLProcessor(config.getProperty("tag.RAW.html.template"), headers);
   }
   
   public static HTMLProcessor getStringProcessor(String subjectTemplate, Map<String, String> headers)
     throws TemplateProcessingException
   {
     return new FreemarkerStringProcessor(subjectTemplate, headers);
   }
 }