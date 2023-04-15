import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.Queue;

public class lamportMessage {
    String messageType;
    int clock;
    int siteNumber;

    public lamportMessage(String messageType, int clock, int siteNumber) {
        this.messageType = messageType;
        this.clock = clock;
        this.siteNumber = siteNumber;
    }


    public ACLMessage sendREQ(int receiver) {
        switch (receiver) {
            case 1:
                // Init message content for site 1
                ACLMessage site1Message = new ACLMessage(ACLMessage.INFORM);
                site1Message.addReceiver(new AID("site1", AID.ISLOCALNAME));
                site1Message.setContent(this.messageType + "," + this.clock + "," + this.siteNumber);
                return site1Message;
            case 2:
                // Init message content for site 2
                ACLMessage site2Message = new ACLMessage(ACLMessage.INFORM);
                site2Message.addReceiver(new AID("site2", AID.ISLOCALNAME));
                site2Message.setContent(this.messageType + "," + this.clock + "," + this.siteNumber);
                return site2Message;
            case 3:
                // Init message content for site 3
                ACLMessage site3Message = new ACLMessage(ACLMessage.INFORM);
                site3Message.addReceiver(new AID("site3", AID.ISLOCALNAME));
                site3Message.setContent(this.messageType + "," + this.clock + "," + this.siteNumber);
                return site3Message;
            default:
                return null;
        }
    }

    public ACLMessage sendACK() {
        switch (this.siteNumber) {
            case 1:
                // Init message content for site 1
                ACLMessage site1Message = new ACLMessage(ACLMessage.INFORM);
                site1Message.addReceiver(new AID("site1", AID.ISLOCALNAME));
                site1Message.setContent("ACK" + "," + this.clock + "," + this.siteNumber);
                return site1Message;
            case 2:
                // Init message content for site 2
                ACLMessage site2Message = new ACLMessage(ACLMessage.INFORM);
                site2Message.addReceiver(new AID("site2", AID.ISLOCALNAME));
                site2Message.setContent("ACK" + "," + this.clock + "," + this.siteNumber);
                return site2Message;
            case 3:
                // Init message content for site 3
                ACLMessage site3Message = new ACLMessage(ACLMessage.INFORM);
                site3Message.addReceiver(new AID("site3", AID.ISLOCALNAME));
                site3Message.setContent("ACK" + "," + this.clock + "," + this.siteNumber);
                return site3Message;
            default:
                return null;
        }
    }

    public ACLMessage[] sendREL() {
        ACLMessage[] rel = new ACLMessage[2];
        switch (this.siteNumber) {
            case 1:
                // Init message content for site 2
                rel[0] = new ACLMessage(ACLMessage.INFORM);
                rel[0].addReceiver(new AID("site2", AID.ISLOCALNAME));
                rel[0].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                // Init message content for site 3
                rel[1] = new ACLMessage(ACLMessage.INFORM);
                rel[1].addReceiver(new AID("site3", AID.ISLOCALNAME));
                rel[1].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                return rel;
            case 2:
                // Init message content for site 1
                rel[0] = new ACLMessage(ACLMessage.INFORM);
                rel[0].addReceiver(new AID("site1", AID.ISLOCALNAME));
                rel[0].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                // Init message content for site 3
                rel[1] = new ACLMessage(ACLMessage.INFORM);
                rel[1].addReceiver(new AID("site3", AID.ISLOCALNAME));
                rel[1].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                return rel;
            case 3:
                // Init message content for site 1
                rel[0] = new ACLMessage(ACLMessage.INFORM);
                rel[0].addReceiver(new AID("site1", AID.ISLOCALNAME));
                rel[0].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                // Init message content for site 3
                rel[1] = new ACLMessage(ACLMessage.INFORM);
                rel[1].addReceiver(new AID("site2", AID.ISLOCALNAME));
                rel[1].setContent("REL" + "," + this.clock + "," + this.siteNumber);
                return rel;
            default:
                return null;
        }
    }

    static public lamportMessage getQueueMessages(String content) {
        String[] message = content.split(",");
        return new lamportMessage(message[0], Integer.parseInt(message[1]), Integer.parseInt(message[2]));
    }

    static public LinkedList<lamportMessage> checkpriority(LinkedList<lamportMessage> Queue, lamportMessage newMessage) {
        // checking the Queue
        int length = Queue.size();
        if (Queue.getFirst() == null) {
            Queue.addFirst(newMessage);
        } else if (newMessage.clock < Queue.getFirst().clock) {
            Queue.addFirst(newMessage);
        } else {
            for (int i = 0; i < length; i++) {
                if (newMessage.clock < Queue.get(i).clock) {
                    Queue.add(i, newMessage);
                    return Queue;
                }
            }
            Queue.addLast(newMessage);
        }
        return Queue;
    }
}
