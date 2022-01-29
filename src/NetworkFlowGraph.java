import java.util.*;

public class NetworkFlowGraph {
    private final int n; //number of nodes in the distributionNetwork
    private final int superSource;
    private final int superSink;
    private int maxFlow=0;
    private final int[] levels;

    static class Connection {
        int start;
        int end;
        int capacity;
        int flow;
        Connection residual;

        public Connection(int start, int end, int capacity) {
            this.start = start;
            this.end = end;
            this.capacity = capacity;
        }
    }
    abstract static class Vertex{
        int id;
        public Vertex(int id) {
            this.id = id;
        }
    }
    //Source vertexes are the bags and the super source.
    static class Source extends Vertex{
        boolean[] property; //0->a,1->b,2->c,3->d,4->e. If exists->True.
        int capacity;

        public Source(int id, boolean[] property,int capacity) {
            super(id);
            this.property = property;
            this.capacity= capacity;
        }
    }
    //Sink Vertex is vehicles and the super sink.
    static class Sink extends Vertex{
        boolean isGreen;    //isGreen=True->Green,isGreen=False->Red
        boolean isTrain;    //isTrain=True->Train,isTrain=False->Reindeer

        public Sink(int id, boolean isGreen, boolean isTrain) {
            super(id);
            this.isGreen = isGreen;
            this.isTrain = isTrain;
        }
    }

    private final List<LinkedList<Connection>> distributionNetwork; //the graph
    private final ArrayList<Source> sources=new ArrayList<>(); //List of sources except super source
    private final ArrayList<Sink> sinks=new ArrayList<>(); //List of sinks except super sink

    //Creating empty distributionNetwork.
    public NetworkFlowGraph(int n,int superSource,int superSink) {
        this.n=n;
        this.superSource=superSource;
        this.superSink=superSink;
        levels = new int[n]; //Create levels array to use it in Dinic's Algorithm

        distributionNetwork = new ArrayList<>(n);
        for (int i = 0; i < n; i++){
            distributionNetwork.add(new LinkedList<>());
        }
    }

    /*
    * Adding both forward and backward edges. Forward edge capacity is assigned as given.
    * Backward edges initially has 0 capacity. Also, I assigned the residual edges to each other to reach them easily.
    * */
    public void addConnection(int start, int end, int capacity) {
        Connection forward= new Connection(start, end, capacity);
        Connection backward= new Connection(end, start, 0);
        forward.residual =backward;
        backward.residual =forward;
        distributionNetwork.get(start).add(forward);
        distributionNetwork.get(end).add(backward);
    }
    public void addSource(int id, boolean a,boolean b,boolean c,boolean d,boolean e,int capacity){
        boolean[] property={a,b,c,d,e};
        sources.add(new Source(id, property, capacity));
    }
    public void addSink(int id, boolean isGreen, boolean isTrain){
        sinks.add(new Sink(id, isGreen, isTrain));
    }

    //Creating middle edges is long process which includes many probability.
    public void addMiddleEdges(){
        for (Source source : sources) {
            for (Sink sink : sinks) {
                //Id of source and sink is determined. Also capacity of the source.
                int sourceId = source.id;
                int sinkId = sink.id;
                int capacity = source.capacity;

                //First properties of the source vertex (bags) are determined.
                boolean a = source.property[0];
                boolean b = source.property[1];
                boolean c = source.property[2];
                boolean d = source.property[3];
                boolean e = source.property[4];
                //Sink properties are determined.
                char[] sinkType = new char[2];
                sinkType[0] = (sink.isGreen) ? 'g' : 'r';//g:green, r:red
                sinkType[1] = (sink.isTrain) ? 't' : 'd';//t:train, d:deer

                //The following 17 different if statement stands for probabilities of different matchings.
                //All possibilities are written separately. The bag types and vehicle types are written below.
                if (!a) {
                    if (b && !c && !d && !e) {//bag type: b
                        if (sinkType[0] == 'g') {//vehicle type: g
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (b && !c && d && !e) {//bag type: bd
                        if (sinkType[0] == 'g' && sinkType[1] == 't') {//vehicle type: gt
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (b && !c && !d && e) {//bag type: be
                        if (sinkType[0] == 'g' && sinkType[1] == 'd') {//vehicle type: gd
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && !d && !e) {//bag type: c
                        if (sinkType[0] == 'r') {//vehicle type: r
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && d && !e) {//bag type: cd
                        if (sinkType[0] == 'r' && sinkType[1] == 't') {//vehicle type: rt
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && !d && e) {//bag type: ce
                        if (sinkType[0] == 'r' && sinkType[1] == 'd') {//vehicle type: rd
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && !c && d && !e) {//bag type: d
                        if (sinkType[1] == 't') {//vehicle type: t
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && !c && !d && e) {//bag type: e
                        if (sinkType[1] == 'd') {//vehicle type: d
                            addConnection(sourceId, sinkId, capacity);
                        }
                    }
                } else {//a is true.
                    capacity = 1; //Property of the a.
                    if (!b && !c && !d && !e) {//bag type: a
                        addConnection(sourceId, sinkId, capacity);
                    } else if (b && !c && !d && !e) {//bag type: ab
                        if (sinkType[0] == 'g') {//vehicle type: g
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (b && !c && d && !e) {//bag type: abd
                        if (sinkType[0] == 'g' && sinkType[1] == 't') {//vehicle type: gt
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (b && !c && !d && e) {//bag type: abe
                        if (sinkType[0] == 'g' && sinkType[1] == 'd') {//vehicle type: gd
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && !d && !e) {//bag type: ac
                        if (sinkType[0] == 'r') {//vehicle type: r
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && d && !e) {//bag type: acd
                        if (sinkType[0] == 'r' && sinkType[1] == 't') {//vehicle type: rt
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && c && !d && e) {//bag type: ace
                        if (sinkType[0] == 'r' && sinkType[1] == 'd') {//vehicle type: rd
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && !c && d && !e) {//bag type: ad
                        if (sinkType[1] == 't') {//vehicle type: t
                            addConnection(sourceId, sinkId, capacity);
                        }
                    } else if (!b && !c && !d && e) {//bag type: ae
                        if (sinkType[1] == 'd') {//vehicle type:d
                            addConnection(sourceId, sinkId, capacity);
                        }
                    }
                }
            }
        }
    }
    //Creating Graph functions are ended here.

    //Algorithm functions starts here.
    public int getMaxFlow() {
        //deadEndOptimizer[]: It is an optimization method to eliminate all the dead ends. They are only seen once.
        int[] deadEndOptimizer = new int[n];

        /*
        * While sink can be reached continue while loop. If it cannot be reached, that
        * means there is no more augmenting flow that can be added. In other words, maxFlow is reached.
        */
        int INFINITY=Integer.MAX_VALUE;
        while (levelize()) {
            Arrays.fill(deadEndOptimizer, 0);

            double augmentingFlow;
            do{
                augmentingFlow = depthFirstSearch(superSource, deadEndOptimizer, INFINITY);
                maxFlow += augmentingFlow;//For each augmenting path found, add them to the max flow.
            }while(augmentingFlow!=0);
        }
        return maxFlow;
    }

    //Following levelize() method stands to form a level distributionNetwork and check if it is possible to reach sink node.
    private boolean levelize() {
        Arrays.fill(levels, -1);
        levels[superSource] = 0; //First node of the distributionNetwork is superSource

        Queue<Integer> bfsQueue = new ArrayDeque<>(n);
        bfsQueue.add(superSource);

        //Following while loop stands for the classic breadth first search algorithm.
        while (!bfsQueue.isEmpty()) {
            int currentNode = bfsQueue.poll();
            //Following for loop is to search all the edges that are outgoing from the current node.
            for (Connection connection : distributionNetwork.get(currentNode)) {
                int remainingCapacity = connection.capacity - connection.flow;

                /*Modification for this BFS is that we can only use edges that have remaining capacity
                * greater than zero since distributionNetwork is a network flow distributionNetwork. Also, nodes are checked whether
                * they are visited or not by looking levels array -1 or not. If there is reachable node
                * levelUp it and add to queue.
                * */
                if (remainingCapacity > 0 && levels[connection.end] == -1) {
                    levels[connection.end] = levelUp(currentNode);
                    bfsQueue.add(connection.end);
                }
            }
        }
        boolean isSinkReached = (levels[superSink] != -1);
        return isSinkReached;
    }


    //Classic depth first search method just with one optimization which is eliminating dead ends.
    private int depthFirstSearch(int currentNode, int[] deadEndOptimizer, int minFlow) {
        if (currentNode == superSink) {
            return minFlow;
        }
        final int numberOfEdges = distributionNetwork.get(currentNode).size();

        while (deadEndOptimizer[currentNode] < numberOfEdges){//While all the edges are visited
            Connection connection = distributionNetwork.get(currentNode).get(deadEndOptimizer[currentNode]);//For  each loop visit another connection that is connected to current node. deadEndOptimizer[currentNode] starts from 0 to distributionNetwork.get(currentNode).size()
            // Only augment flow if there is remaining capacity and departure array is in the next level
            // because the idea behind Dinic's algorithm is going only towards sink.
            int remainingCapacity = connection.capacity- connection.flow;
            if (remainingCapacity > 0 && levels[connection.end] == levels[currentNode] + 1) {
                int bottleNeck = depthFirstSearch(connection.end, deadEndOptimizer, Math.min(minFlow, remainingCapacity));//Recursive Call, Attention that minFlow is infinity initially.
                // If we can find bottleNeck value for the path, add it to the flows to both forward direction and backward direction.
                if (bottleNeck > 0) {
                    connection.flow = connection.flow + bottleNeck;
                    connection.residual.flow = connection.residual.flow - bottleNeck;
                    return bottleNeck;
                }
            }
            deadEndOptimizer[currentNode]++; //If from the current node i'th node is not reacheble or is reached increase it one not to try visiting it twice or more.
        }
        return 0;
    }
    private int levelUp(int vertex){
        return levels[vertex] + 1;
    }
}
