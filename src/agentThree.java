import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.LinkedList;

public class agentThree extends Agent {
    LinkedList<Message> file = new LinkedList<Message>();

    public void setup(){
        System.out.println("Im agent 3");
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage message = myAgent.receive();
                if (message == null ){
                    ACLMessage messageR = new ACLMessage(ACLMessage.INFORM);
                    messageR.addReceiver(new AID("site1",AID.ISLOCALNAME));
                  //  messageR.setContent();

                }else {
                    Message recievedMessage = Message.getQueueMessages(message.getContent());
                    file.add(recievedMessage);
                    file = Message.checkpriority(file, recievedMessage);
                    block();
                }
            }
        });
    }
}
