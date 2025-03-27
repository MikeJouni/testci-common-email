package org.apache.commons.mail;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
// CI TEST
// Test class for validating email functionality
public class EmailValidationTest {

    private Email mailClient;

    @Before
    public void initializeTest() {
        // Create a fresh SimpleEmail instance before each test
        mailClient = new SimpleEmail();
    }

    @After
    public void cleanupTest() {
        // Clean up after tests to avoid memory leaks
        mailClient = null;
    }

    @Test
    public void verifyBccAdditionWithValidAddresses() throws Exception {
        String[] validAddresses = {"user1@example.org", "user2@example.org"};

        mailClient.addBcc(validAddresses);

        // Check if the addresses were added correctly to the BCC list
        String firstAddress = mailClient.getBccAddresses().get(0).getAddress();
        String secondAddress = mailClient.getBccAddresses().get(1).getAddress();

        Assert.assertEquals(validAddresses[0], firstAddress);
        Assert.assertEquals(validAddresses[1], secondAddress);
    }

    @Test(expected = EmailException.class)
    public void verifyBccAdditionWithNullAddresses() throws Exception {
        // Should throw an exception when null is passed
        String[] nullAddresses = null;
        mailClient.addBcc(nullAddresses);
    }

    @Test(expected = EmailException.class)
    public void verifyBccAdditionWithEmptyArray() throws Exception {
        // Should throw an exception when empty array is passed
        String[] emptyAddresses = {};
        mailClient.addBcc(emptyAddresses);
    }

    @Test
    public void verifyCcAdditionWithValidAddress() throws Exception {
        String validAddress = "user@example.org";

        mailClient.addCc(validAddress);

        // Verify the CC address was added correctly
        String retrievedAddress = mailClient.getCcAddresses().get(0).getAddress();
        Assert.assertEquals(validAddress, retrievedAddress);
    }

    @Test(expected = EmailException.class)
    public void verifyCcAdditionWithInvalidAddress() throws Exception {
        // Invalid email format should cause an exception
        String invalidAddress = "not an email address";

        mailClient.addCc(invalidAddress);
    }

    @Test
    public void verifyHeaderAdditionWithValidParameters() throws Exception {
        // Testing the header addition functionality
        String headerName = "X-Custom-Header";
        String headerValue = "custom-value";
        mailClient.addHeader(headerName, headerValue);
        // No assertion needed as method doesn't return a value
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyHeaderAdditionWithNullName() throws Exception {
        // Header name can't be null
        String nullName = null;
        String headerValue = "custom-value";
        mailClient.addHeader(nullName, headerValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyHeaderAdditionWithEmptyValue() throws Exception {
        // Header value can't be empty
        String headerName = "X-Custom-Header";
        String emptyValue = "";
        mailClient.addHeader(headerName, emptyValue);
    }

    @Test
    public void verifyReplyToAdditionWithValidParameters() throws Exception {
        // Testing reply-to functionality with both address and display name
        String validAddress = "reply@example.org";
        String displayName = "Reply Handler";
        mailClient.addReplyTo(validAddress, displayName);

        String retrievedAddress = mailClient.getReplyToAddresses().get(0).getAddress();
        String retrievedName = mailClient.getReplyToAddresses().get(0).getPersonal();
        Assert.assertEquals(validAddress, retrievedAddress);
        Assert.assertEquals(displayName, retrievedName);
    }

    @Test(expected = EmailException.class)
    public void verifyReplyToAdditionWithInvalidAddress() throws Exception {
        // Invalid email format should cause an exception
        String invalidAddress = "not an email";
        String displayName = "Reply Handler";
        mailClient.addReplyTo(invalidAddress, displayName);
    }

    @Test
    public void verifyMimeMessageCreationWithValidInputs() throws Exception {
        // Complete email setup for MIME message creation
        mailClient.setHostName("mail.example.org");
        mailClient.setFrom("sender@example.org");
        mailClient.addTo("recipient@example.org");
        mailClient.addCc("cc-recipient@example.org");
        mailClient.addBcc("bcc-recipient@example.org");
        mailClient.addReplyTo("reply-handler@example.org");
        mailClient.setContent("Email content for testing", EmailConstants.TEXT_PLAIN);
        mailClient.addHeader("X-Custom", "TestValue");

        mailClient.buildMimeMessage();

        MimeMessage message = mailClient.getMimeMessage();
        Assert.assertEquals(EmailConstants.TEXT_PLAIN, message.getContentType());
    }

    @Test(expected = IllegalStateException.class)
    public void verifyMimeMessageCreationWhenAlreadyBuilt() throws Exception {
        // Setting up email for MIME message test
        mailClient.setHostName("mail.example.org");
        mailClient.setFrom("sender@example.org");
        mailClient.addTo("recipient@example.org");
        mailClient.addCc("cc-recipient@example.org");
        mailClient.addBcc("bcc-recipient@example.org");
        mailClient.addReplyTo("reply-handler@example.org");
        mailClient.setContent("Email content for testing", EmailConstants.TEXT_PLAIN);
        mailClient.addHeader("X-Custom", "TestValue");

        mailClient.buildMimeMessage();
        mailClient.buildMimeMessage(); // Should throw exception on second build attempt
    }

    @Test(expected = EmailException.class)
    public void verifyMimeMessageCreationWithoutSender() throws Exception {
        // Can't build a message without sender
        mailClient.setHostName("mail.example.org");
        mailClient.addTo("recipient@example.org");

        mailClient.buildMimeMessage();
    }

    @Test(expected = EmailException.class)
    public void verifyMimeMessageCreationWithoutRecipient() throws Exception {
        // Can't build a message without recipient
        mailClient.setHostName("mail.example.org");
        mailClient.setFrom("sender@example.org");

        mailClient.buildMimeMessage();
    }

    @Test
    public void verifyHostNameWithSessionCreated() throws Exception {
        // Test hostname setting with session creation
        String hostname = "mail.example.org";
        mailClient.setHostName(hostname);

        Session session = mailClient.getMailSession();

        Assert.assertEquals(hostname, session.getProperty(EmailConstants.MAIL_HOST));
        Assert.assertEquals(hostname, mailClient.getHostName());
    }

    @Test
    public void verifyHostNameWithoutSessionCreation() throws Exception {
        // Test hostname setting without session creation
        String hostname = "mail.example.org";
        mailClient.setHostName(hostname);

        Assert.assertEquals(hostname, mailClient.getHostName());
    }

    @Test
    public void verifyEmptyHostNameBehavior() throws Exception {
        // Empty hostname should be treated as null
        String emptyHostname = "";
        mailClient.setHostName(emptyHostname);

        Assert.assertNull(mailClient.getHostName());
    }

    @Test
    public void verifyMailSessionWithValidHostName() throws Exception {
        // Testing mail session creation with valid hostname
        String hostname = "mail.example.org";
        mailClient.setHostName(hostname);

        Session session = mailClient.getMailSession();
        String sessionHostName = session.getProperty(EmailConstants.MAIL_HOST);

        Assert.assertNotNull(session);
        Assert.assertEquals(hostname, sessionHostName);
    }

    @Test(expected = EmailException.class)
    public void verifyMailSessionWithEmptyHostname() throws Exception {
        // Can't create a mail session without valid hostname
        mailClient.setHostName("");
        mailClient.getMailSession();
    }

    @Test
    public void verifySenderAddressWithValidEmail() throws Exception {
        // Testing sender email address setting
        String validAddress = "sender@example.org";

        mailClient.setFrom(validAddress);

        String retrievedAddress = mailClient.getFromAddress().getAddress();
        Assert.assertEquals(validAddress, retrievedAddress);
    }

    @Test(expected = EmailException.class)
    public void verifySenderAddressWithInvalidEmail() throws Exception {
        // Invalid email format should be rejected
        String invalidAddress = "not an email address";
        mailClient.setFrom(invalidAddress);
    }

    @Test
    public void verifySentDateRetrieval() throws Exception {
        // Testing sent date functionality
        Date testDate = new Date(0);
        mailClient.setSentDate(testDate);

        Date retrievedDate = mailClient.getSentDate();

        Assert.assertEquals(testDate.getTime(), retrievedDate.getTime());
    }

    @Test
    public void verifySocketTimeoutConfiguration() throws Exception {
        // Verify socket timeout configuration works
        int timeoutValue = 6000;
        mailClient.setSocketConnectionTimeout(timeoutValue);

        Assert.assertEquals(timeoutValue, mailClient.getSocketConnectionTimeout());
    }
}