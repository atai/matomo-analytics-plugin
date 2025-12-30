package io.jenkins.plugins.matomoanalytics;

import hudson.model.PageDecorator;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for configuration and persistence of {@link MatomoPageDecorator}.
 */
@WithJenkins
public class MatomoPageDecoratorConfigurationTest {

    @Test
    public void testDefaultValues(JenkinsRule jenkinsRule) {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);
        
        // Test default values
        assertNull(decorator.getMatomoSiteID(), "Default site ID should be null");
        assertNull(decorator.getMatomoServer(), "Default server should be null");
        assertNull(decorator.getMatomoPath(), "Default path should be null");
        assertNull(decorator.getMatomoPhp(), "Default PHP file should be null");
        assertNull(decorator.getMatomoJs(), "Default JS file should be null");
        assertTrue(decorator.isMatomoUseHttps(), "Default should use HTTPS");
        assertFalse(decorator.isMatomoSendUserID(), "Default should not send user ID");
    }

    @Test
    public void testConfigurationPersistence(JenkinsRule jenkinsRule) throws Exception {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Set configuration
        String siteID = "99";
        String server = "test-matomo.example.com";
        String path = "/analytics/";
        String php = "track.php";
        String js = "track.js";

        decorator.setMatomoSiteID(siteID);
        decorator.setMatomoServer(server);
        decorator.setMatomoPath(path);
        decorator.setMatomoPhp(php);
        decorator.setMatomoJs(js);
        decorator.setMatomoUseHttps(false);
        decorator.setMatomoSendUserID(true);
        decorator.save();

        // Reload Jenkins instance to test persistence
        jenkinsRule.getInstance().reload();

        // Verify configuration persisted
        MatomoPageDecorator reloadedDecorator = jenkinsRule.getInstance()
                .getExtensionList(PageDecorator.class).get(MatomoPageDecorator.class);

        assertEquals(siteID, reloadedDecorator.getMatomoSiteID(), "Site ID should persist");
        assertEquals(server, reloadedDecorator.getMatomoServer(), "Server should persist");
        assertEquals(path, reloadedDecorator.getMatomoPath(), "Path should persist");
        assertEquals(php, reloadedDecorator.getMatomoPhp(), "PHP file should persist");
        assertEquals(js, reloadedDecorator.getMatomoJs(), "JS file should persist");
        assertFalse(reloadedDecorator.isMatomoUseHttps(), "HTTPS setting should persist");
        assertTrue(reloadedDecorator.isMatomoSendUserID(), "Send user ID setting should persist");
    }

    @Test
    public void testNullValuesHandling(JenkinsRule jenkinsRule) {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // Set some values
        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("server.com");

        // Set to null
        decorator.setMatomoSiteID(null);
        decorator.setMatomoServer(null);
        decorator.setMatomoPath(null);
        decorator.setMatomoPhp(null);
        decorator.setMatomoJs(null);

        assertNull(decorator.getMatomoSiteID(), "Site ID should be null");
        assertNull(decorator.getMatomoServer(), "Server should be null");
        assertNull(decorator.getMatomoPath(), "Path should be null");
        assertNull(decorator.getMatomoPhp(), "PHP file should be null");
        assertNull(decorator.getMatomoJs(), "JS file should be null");
    }

    @Test
    public void testEmptyStringValues(JenkinsRule jenkinsRule) {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoSiteID("");
        decorator.setMatomoServer("");
        decorator.setMatomoPath("");

        assertEquals("", decorator.getMatomoSiteID(), "Empty site ID should be stored");
        assertEquals("", decorator.getMatomoServer(), "Empty server should be stored");
        assertEquals("", decorator.getMatomoPath(), "Empty path should be stored");
    }

    @Test
    public void testProtocolStringChanges(JenkinsRule jenkinsRule) {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        decorator.setMatomoUseHttps(true);
        assertEquals("https://", decorator.getProtocolString(), "Should return https://");

        decorator.setMatomoUseHttps(false);
        assertEquals("http://", decorator.getProtocolString(), "Should return http://");

        // Toggle back
        decorator.setMatomoUseHttps(true);
        assertEquals("https://", decorator.getProtocolString(), "Should return https:// again");
    }

    @Test
    public void testMultipleConfigurationChanges(JenkinsRule jenkinsRule) {
        MatomoPageDecorator decorator = jenkinsRule.getInstance().getExtensionList(PageDecorator.class)
                .get(MatomoPageDecorator.class);

        // First configuration
        decorator.setMatomoSiteID("1");
        decorator.setMatomoServer("server1.com");
        assertEquals("1", decorator.getMatomoSiteID());
        assertEquals("server1.com", decorator.getMatomoServer());

        // Change configuration
        decorator.setMatomoSiteID("2");
        decorator.setMatomoServer("server2.com");
        assertEquals("2", decorator.getMatomoSiteID());
        assertEquals("server2.com", decorator.getMatomoServer());

        // Change again
        decorator.setMatomoSiteID("3");
        decorator.setMatomoServer("server3.com");
        assertEquals("3", decorator.getMatomoSiteID());
        assertEquals("server3.com", decorator.getMatomoServer());
    }
}

