package Com.base.FunctionLibarary;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * MQkeywords - IBM MQ messaging keyword library (JMS API).
 * Flow:
 *   jms_messageConnection(host, port, channel, qmgr, user, pass);
 *   jms_sendMessage("MY.QUEUE", "payload");
 *   closeConnection();
 */
public class MQkeywords {

    private static final Logger log = LogManager.getLogger(MQkeywords.class);

    private static Connection connection;
    static Session session;

    // ===================================================================
    //  CONNECTIONS
    // ===================================================================

    // open a connection WITHOUT username/password authentication
    public static void messageConnectionWithoutAuthentication(String host, int port,
                                                              String channel, String queueManager) {
        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory cf = ff.createConnectionFactory();

            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
            cf.setIntProperty(WMQConstants.WMQ_PORT, port);
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManager);

            connection = cf.createConnection();   // no user/pass
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            log.info("MQ connection (no auth) opened to " + queueManager);
        } catch (JMSException e) {
            log.error("messageConnectionWithoutAuthentication failed: " + e.getMessage());
        }
    }

    // open a connection WITH username/password authentication
    public static void jms_messageConnection(String host, int port, String channel,
                                             String queueManager, String user, String password) {
        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory cf = ff.createConnectionFactory();

            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
            cf.setIntProperty(WMQConstants.WMQ_PORT, port);
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManager);
            cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            cf.setStringProperty(WMQConstants.USERID, user);
            cf.setStringProperty(WMQConstants.PASSWORD, password);

            connection = cf.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            log.info("MQ connection (auth) opened to " + queueManager + " as " + user);
        } catch (JMSException e) {
            log.error("jms_messageConnection failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  MESSAGE OBJECT
    // ===================================================================

    // create a TextMessage object from a string payload
    public static TextMessage createMessageObject(String payload) {
        try {
            return session.createTextMessage(payload);
        } catch (JMSException e) {
            log.error("createMessageObject failed: " + e.getMessage());
            return null;
        }
    }

    // assign custom JMS string properties (headers) to a message
    public static void assignFewProperties(Message message, Map<String, String> properties) {
        try {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                message.setStringProperty(entry.getKey(), entry.getValue());
            }
            log.info("Assigned " + properties.size() + " properties to message");
        } catch (JMSException e) {
            log.error("assignFewProperties failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  SEND
    // ===================================================================

    // low-level send: put a prepared Message onto the given queue
    public static void sendMessage(String queueName, Message message) {
        try {
            Destination destination = session.createQueue("queue:///" + queueName);
            MessageProducer producer = session.createProducer(destination);
            producer.send(message);
            producer.close();
            log.info("Message sent to queue: " + queueName);
        } catch (JMSException e) {
            log.error("sendMessage failed: " + e.getMessage());
        }
    }

    // convenience: build a TextMessage from a string and send it
    public static void jms_sendMessage(String queueName, String payload) {
        TextMessage msg = createMessageObject(payload);
        if (msg != null) sendMessage(queueName, msg);
    }

    // send in IBM MQ format: sets MQMD-style properties so non-JMS MQ consumers
    // (e.g. mainframe / COBOL apps) can read it as a plain MQSTR message
    public static void jms_sendMessage_ibm_format(String queueName, String payload) {
        try {
            Destination destination = session.createQueue("queue:///" + queueName
                    + "?targetClient=1");   // targetClient=1 => MQ (non-JMS) format, no RFH2 header

            TextMessage msg = session.createTextMessage(payload);
            MessageProducer producer = session.createProducer(destination);
            producer.send(msg);
            producer.close();
            log.info("Message sent in IBM MQ (non-JMS) format to queue: " + queueName);
        } catch (JMSException e) {
            log.error("jms_sendMessage_ibm_format failed: " + e.getMessage());
        }
    }

    // ===================================================================
    //  CLEANUP
    // ===================================================================

    public static void closeConnection() {
        try {
            if (session != null) session.close();
            if (connection != null) connection.close();
            log.info("MQ connection closed");
        } catch (JMSException e) {
            log.error("closeConnection failed: " + e.getMessage());
        }
    }
}