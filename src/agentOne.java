import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;
import java.util.Queue;


/**
 * @author ${ BENMOUSSA Younes}
 * @mailto : devBenmoussYounes@gmail.com
 * @created 13/04/2023, jeudi
 * @project ${Simulation of the Lamport synchronisation method}
 */

// Stack(Pile) LIFO
// Queue(File) FIFO
 // add = enqueue, Offer()
 // remove = dequeue, poll()

public class agentOne extends Agent {
    Message REQ = new Message("REQ",2,1);
    int ACK = 0;
    boolean REL = true;
    LinkedList<Message> file = new LinkedList<Message>();
    public void setup(){
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = myAgent.receive();
                if (message == null ) {
                    /**
                     Site 1 broadcast ...
                     * */

                    // Init message content for site 2
                    ACLMessage site2Message = new ACLMessage(ACLMessage.INFORM);
                    site2Message.addReceiver(new AID("site2", AID.ISLOCALNAME));
                    site2Message.setContent(REQ.messageType +","+ REQ.clock +","+ REQ.siteNumber);

                    // Init message content for site 3
                    ACLMessage site3Message = new ACLMessage(ACLMessage.INFORM);
                    site3Message.addReceiver(new AID("site3", AID.ISLOCALNAME));
                    site3Message.setContent(REQ.messageType +","+ REQ.clock +","+ REQ.siteNumber);

                    // Site 1 broadcast its request to enter the Critical Section
                    send(site2Message);
                    send(site3Message);
                    file.add(REQ);
                    block();
                }else{
                    String content = String.valueOf(message.getContent());
                   // System.out.println("I got this"+content);

                }
            }
        });

        // Waiting ACK ...
        if (ACK == 3 && REL) {
            addBehaviour(new CyclicBehaviour() {
                @Override
                public void action() {

                }
            });
        }
        }
    }

