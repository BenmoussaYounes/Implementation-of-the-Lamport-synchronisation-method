import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class agentTwo extends Agent {
    Message REQ = new Message("REQ",2,1);
    int ACK = 0;
    boolean REL = true;
    LinkedList<Message> file = new LinkedList<Message>();
    public void setup(){
     System.out.println("Im Agent 2");
     addBehaviour(new CyclicBehaviour() {
         @Override
         public void action() {
             ACLMessage message = myAgent.receive();
             if (message == null ){
                 /**
                  Site 2 broadcast ...
                  * */

                 // Init message content for site 1
                 ACLMessage site1Message = new ACLMessage(ACLMessage.INFORM);
                 site1Message.addReceiver(new AID("site1", AID.ISLOCALNAME));
                 site1Message.setContent(REQ.messageType +","+ REQ.clock +","+ REQ.siteNumber);

                 // Init message content for site 3
                 ACLMessage site3Message = new ACLMessage(ACLMessage.INFORM);
                 site3Message.addReceiver(new AID("site3", AID.ISLOCALNAME));
                 site3Message.setContent(REQ.messageType +","+ REQ.clock +","+ REQ.siteNumber);

                 // Site 1 broadcast its request to enter the Critical Section
                 send(site1Message);
                 send(site3Message);
                 file.add(REQ);
                 block();
             }else {

             }
         }
     });
    }

}
