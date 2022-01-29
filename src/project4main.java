import java.io.*;
import java.util.Scanner;

public class project4main {
    public static void main(String[] args) throws FileNotFoundException {
        //Scanning inputs starts.
        File inputFile = new File("D:\\Kullanıcılar\\canko\\OneDrive\\Masaüstü\\inputs\\input_5.txt");
        File outputFile = new File("D:\\Kullanıcılar\\canko\\OneDrive\\Masaüstü\\output.txt");
        Scanner scan = new Scanner(inputFile);

        final int tG= scan.nextInt(); //Number of green trains
        for (int i = 0; i < tG; i++) {
            scan.nextInt();
        }

        final int tR=scan.nextInt(); //Number of red trains
        for (int i = 0; i < tR; i++) {
            scan.nextInt();
        }

        final int rG=scan.nextInt(); //Number of green reindeers
        for (int i = 0; i < rG; i++) {
            scan.nextInt();
        }

        final int rR=scan.nextInt(); //Number of red reindeers
        for (int i = 0; i < rR; i++) {
            scan.nextInt();
        }
        final int bags=scan.nextInt(); //Number of bags
        final int n=tG+tR+rG+rR+bags+2; //Total number of nodes. +2 is for super source and super sink
        final int superSink=n-1; //Super sink id
        final int superSource=0; //Super source id
        NetworkFlowGraph networkFlowGraph=new NetworkFlowGraph(n,superSource,superSink); //Initialize graph

        Scanner scan2 = new Scanner(inputFile);

        int sinkId=superSink;
        int sourceId=superSource;

        //Adding sinks and edges from sinks to super sink.
        scan2.nextInt(); //To skip the line with tG
        for (int i = 0; i < tG; i++) {
            sinkId--;
            int capacity=scan2.nextInt();
            networkFlowGraph.addSink(sinkId,true,true);//tG:Green,Train
            networkFlowGraph.addConnection(sinkId,superSink,capacity);
        }
        //Adding sinks and edges from sinks to super sink.
        scan2.nextInt(); //To skip the line with tR
        for (int i = 0; i < tR; i++) {
            sinkId--;
            int capacity=scan2.nextInt();
            networkFlowGraph.addSink(sinkId,false,true);//tR:Red,Train
            networkFlowGraph.addConnection(sinkId,superSink,capacity);
        }
        //Adding sinks and edges from sinks to super sink.
        scan2.nextInt(); //To skip the line with rG
        for (int i = 0; i < rG; i++) {
            sinkId--;
            int capacity=scan2.nextInt();
            networkFlowGraph.addSink(sinkId,true,false);//rG:Green,Reindeer
            networkFlowGraph.addConnection(sinkId,superSink,capacity);
        }
        //Adding sinks and edges from sinks to super sink.
        scan2.nextInt(); //To skip the line with rR
        for (int i = 0; i < rR; i++) {
            sinkId--;
            int capacity=scan2.nextInt();
            networkFlowGraph.addSink(sinkId,false,false);//rR:Red,Reindeer
            networkFlowGraph.addConnection(sinkId,superSink,capacity);
        }
        scan2.nextInt(); //To skip the line with bag

        //Adding sources and edges from super source to sources.
        int totalGifts=0;

        for (int i = 0; i < bags; i++) {
            String token=scan2.next();//Bag type e.g. ab,ad,ce,abe
            String token2=scan2.next();//Capacity
            sourceId++;

            //According to input, bag type created
            boolean a=false,b=false,c=false,d=false,e=false;
            for(int j=0; j<token.length();j++){
                char x = token.charAt(j);
                switch (x) {
                    case 'a':
                        a=true;
                        break;
                    case 'b':
                        b=true;
                        break;
                    case 'c':
                        c=true;
                        break;
                    case 'd':
                        d=true;
                        break;
                    case 'e':
                        e=true;
                        break;
                    default:
                        break;
                }
            }
            int capacity= Integer.parseInt(token2);
            networkFlowGraph.addSource(sourceId,a,b,c,d,e,capacity);

            networkFlowGraph.addConnection(superSource,sourceId,capacity);
            totalGifts+=capacity;
        }
        networkFlowGraph.addMiddleEdges();//Creating edges between sources and sinks.
        //Graph construction part ends.
        int minDistrubutedGifts=totalGifts-networkFlowGraph.getMaxFlow();

        //Following try and catch stands for outputting part.
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write( String.valueOf(minDistrubutedGifts));
            writer.close();
        } catch (IOException e){
            System.out.println("Catch - An error occurred.");
            e.printStackTrace();
        }

    }
}