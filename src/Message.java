import java.util.LinkedList;
import java.util.Queue;

public class Message {
    String messageType;
    int clock;
    int siteNumber;

    public Message(String messageType, int clock, int siteNumber) {
        this.messageType = messageType;
        this.clock = clock;
        this.siteNumber = siteNumber;
    }

    static public Message getQueueMessages(String content){
        String[] message = new String[3];
        message = content.split(",");
        return new Message(message[0],Integer.parseInt(message[1]),Integer.parseInt(message[2]));
    }

    static public LinkedList<Message> checkpriority(LinkedList<Message> Queue, Message message){

        // checking the Queue
        if(Queue.peek() != null && message.clock > Queue.peek().clock){
                Queue.add(Queue.peek());
                Queue.addFirst(message);
        }else{
            Queue.add(message);
        }

        return Queue;
    }
}
