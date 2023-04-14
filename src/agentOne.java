import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;


/**
 * @author ${ BENMOUSSA Younes}
 * @email : devBenmoussYounes@gmail.com
 * @created 13/04/2023
 * @project ${Simulation of the Lamport synchronisation method}
 */

// Stack(Pile) LIFO
// Queue(File) FIFO
// add = enqueue, Offer()
// remove = dequeue, poll()

public class agentOne extends Agent {
    lamportMessage REQ = new lamportMessage("REQ", 3, 1);
    int ACK = 0;
    LinkedList<lamportMessage> Queue = new LinkedList<lamportMessage>();

    public void setup() {
        /**
         Site 1 broadcast its request to enter the Critical Section  ...
         * */
        // Sending REQ
        send(REQ.sendREQ(2));
        send(REQ.sendREQ(3));
        Queue.add(REQ);
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = myAgent.receive();
                if (message != null) {
                    // Building the Message Object from the received message
                    lamportMessage receivedLamportMessage = lamportMessage.getQueueMessages(message.getContent());
                    // ---
                    switch (receivedLamportMessage.messageType) {
                        case "ACK":
                            // Updating ACK
                            ACK += 1;
                            System.out.println("Site 1 Ack number: " + ACK);
                            break;
                        case "REQ":
                            // Updating Queue
                            lamportMessage.checkpriority(Queue, receivedLamportMessage);
                            // Sending ACK
                            send(receivedLamportMessage.sendACK());
                            break;
                        case "REL":
                            if (receivedLamportMessage.siteNumber == Queue.getFirst().siteNumber) {
                                // Updating Queue
                                Queue.removeFirst();
                            }
                            break;
                    }
                } else {
                    System.out.println("Site 1 Waiting ...");
                    this.block();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Waiting ACK ...
                if (ACK == 2 && !Queue.isEmpty() && Queue.getFirst().siteNumber == 1) {
                    /**
                     * Accessing the critical section
                     * */
                    System.out.println("-----------------------------------");
                    System.out.println("Site 1 Consuming Critical Section");
                    System.out.println("-----------------------------------");
                    //Updating Queue
                    ACLMessage[] rel = Queue.getFirst().sendREL();
                    Queue.removeFirst();
                    //Sending REL
                    send(rel[0]);
                    send(rel[1]);
                    ACK = 0;
                }
            }
        });
    }

}

