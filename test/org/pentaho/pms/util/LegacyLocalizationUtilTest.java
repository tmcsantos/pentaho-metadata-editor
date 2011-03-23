package org.pentaho.pms.util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.util.LocalizationUtil;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.pms.core.CWM;
import org.pentaho.pms.factory.CwmSchemaFactory;
import org.pentaho.pms.factory.CwmSchemaFactoryInterface;
import org.pentaho.pms.schema.SchemaMeta;

public class LegacyLocalizationUtilTest {
  
  @SuppressWarnings("deprecation")
  private SchemaMeta loadLegacyMetadataModel(String domainName, String file) throws Exception {
    KettleEnvironment.init(false);
    CWM cwm = null;

    cwm = CWM.getInstance(domainName, true);
    Assert.assertNotNull("CWM singleton instance is null", cwm);
    cwm.importFromXMI(file);    

    CwmSchemaFactoryInterface cwmSchemaFactory = new CwmSchemaFactory();

    return cwmSchemaFactory.getSchemaMeta(cwm);
  }
  
  @SuppressWarnings("deprecation")
  @Test
  public void testLegacyLocalization() throws Exception {
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = localeUtil.exportLocalizedProperties(schemaMeta, locale);
    
    Assert.assertEquals(275, props.size());
    Assert.assertEquals("Customer", props.get("[LogicalModel-BV_ORDERS].[Category-BC_CUSTOMER_W_TER_].[name]"));
    Assert.assertFalse("IPhysicalModel-null should not exist as such",props.containsKey("[IPhysicalModel-null].[PT_DEPARTMENT_MANAGERS].[MANAGER_NAME].[name]"));
  }
  
  @Test
  public void testLegacyLocalizationNullSM() throws Exception {
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = null;
    Exception ex = null;
    
    try {
      localeUtil.exportLocalizedProperties(null, locale);
    } catch(IllegalArgumentException e) {
      ex = e;
    }
    
    Assert.assertNotNull(ex);
    Assert.assertEquals(ex.getClass(), IllegalArgumentException.class);
    Assert.assertEquals("Parameter \"schemaMeta\" MUST not be null", ex.getMessage());
  }
  
  @Test
  public void testLegacyLocalizationNullLocale() throws Exception {
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "en_US";
    Properties props = null;
    Exception ex = null;
    
    try {
      localeUtil.exportLocalizedProperties(schemaMeta, null);
    } catch(IllegalArgumentException e) {
      ex = e;
    }
    
    Assert.assertNotNull(ex);
    Assert.assertEquals(ex.getClass(), IllegalArgumentException.class);
    Assert.assertEquals("Parameter \"locale\" MUST not be null", ex.getMessage());
  }
  
  @Test
  public void testLegacyLocalizationUnknownLocale() throws Exception {
    // Expected results should be the same as en_US as it is the default for non-overidden values
    SchemaMeta schemaMeta = loadLegacyMetadataModel("Steel Wheels", "package-res/samples/steel-wheels.xmi");
    LegacyLocalizationUtil localeUtil = new LegacyLocalizationUtil();
    
    String locale = "abc_XYZ";
    Properties props = localeUtil.exportLocalizedProperties(schemaMeta, locale);
    
    Assert.assertEquals(275, props.size());
    Assert.assertEquals("Customer", props.get("[LogicalModel-BV_ORDERS].[Category-BC_CUSTOMER_W_TER_].[name]"));
  }
}
