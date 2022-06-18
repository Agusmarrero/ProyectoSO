package MLQ;

import Model.Order;
import Statistics.Statistics;
import Threads.TClock;
import java.util.Arrays;
import java.util.concurrent.Semaphore;


public class MLQ implements Runnable{
    private static MLQ instance;
    private FCFSOrder vipOrders = new FCFSOrder();
    private FCFSOrder fastFoodOrders = new FCFSOrder();
    private FCFSOrder mediumFoodOrders = new FCFSOrder();
    private FCFSOrder slowFoodOrders = new FCFSOrder();

    private FCFSOrder[] queuesPlanner = {
            vipOrders, fastFoodOrders, mediumFoodOrders, slowFoodOrders
    };

    public static MLQ getInstance() {
        if (instance == null) {
            instance = new MLQ();
        }
        return instance;
    }

    private final Semaphore solicitudes = new Semaphore(0);
    private final Semaphore solOrder = new Semaphore(0);
    private final Semaphore order = new Semaphore(0);

    public void addOrder(Order order) throws InterruptedException {
        int serviceTime = order.getOrderDescription().getServiceTime();
        int vipNumber = order.getClient().getClientType();
        if (vipNumber == 1) {
            vipOrders.push(order);
        } else {
            if (serviceTime < 20) {
                fastFoodOrders.push(order);

            } else if (serviceTime < 40) {
                mediumFoodOrders.push(order);
            } else {
                slowFoodOrders.push(order);
            }
        }
        //DataHandler.getInstance().release();
    }

    public Order nextOrder() throws InterruptedException {
        for (FCFSOrder queue : queuesPlanner) {
            Order order = queue.pop();
            if (order != null) {
                return order;
            }
        }
        return null;
    }

    public Order nextOrder(FCFSOrder orders) throws InterruptedException {
        Order order = orders.pop();
        if(order != null) {
            return order;
        }
        return null;
        }


    public FCFSOrder getFastFoodOrders() {
        return fastFoodOrders;
    }

    public void setFastFoodOrders(FCFSOrder fastFoodOrders) {
        this.fastFoodOrders = fastFoodOrders;
    }

    public FCFSOrder getMediumFoodOrders() {
        return mediumFoodOrders;
    }

    public void setMediumFoodOrders(FCFSOrder mediumFoodOrders) {
        this.mediumFoodOrders = mediumFoodOrders;
    }

    public FCFSOrder getSlowFoodOrders() {
        return slowFoodOrders;
    }

    public void setSlowFoodOrders(FCFSOrder slowFoodOrders) {
        this.slowFoodOrders = slowFoodOrders;
    }

    public FCFSOrder getVipOrders() {
        return vipOrders;
    }

    public void setVipOrders(FCFSOrder vipOrders) {
        this.vipOrders = vipOrders;
    }

    public FCFSOrder[] getQueuesPlanner() {
        return queuesPlanner;
    }

    public void setQueuesPlanner(FCFSOrder[] queuesPlanner) {
        this.queuesPlanner = queuesPlanner;
    }

    @Override
    public String toString() {
        return "MLQ [fastFoodOrders=" + fastFoodOrders + ", mediumFoodOrders=" + mediumFoodOrders + ", order=" + order
                + ", queuesPlanner=" + Arrays.toString(queuesPlanner) + ", slowFoodOrders=" + slowFoodOrders
                + ", vipOrders=" + vipOrders + "]";
    }

    @Override
    public void run() {
        while (TClock.getInstance().getMoment()<2000000){
            try {
                    while(!vipOrders.isEmpty()){
                        Order order = MLQ.getInstance().nextOrder(vipOrders);
                        if (order != null) {
                            this.processVipOrder(order);
                        }
                    }
                    while(!fastFoodOrders.isEmpty()) {
                        if(!vipOrders.isEmpty()){
                            Order order = MLQ.getInstance().nextOrder(vipOrders);
                            if(order != null){
                                this.processVipOrder(order);
                            }
                        }
                        else  {
                            Order order = MLQ.getInstance().nextOrder(fastFoodOrders);
                            if(order!=null){
                                this.processFastOrder(order);
                            }
                        }
                    }


                    while(!mediumFoodOrders.isEmpty()){
                        if(!vipOrders.isEmpty()){
                            Order order = MLQ.getInstance().nextOrder(vipOrders);
                            if(order != null){
                                processVipOrder(order);
                            }
                        }
                        else  {
                            Order order = MLQ.getInstance().nextOrder(mediumFoodOrders);
                            if(order!=null){
                                this.processMediumOrder(order);
                            }
                        }
                    }

                    while(!slowFoodOrders.isEmpty()){
                        if(!vipOrders.isEmpty()){
                            Order order = MLQ.getInstance().nextOrder(vipOrders);
                            if(order != null){
                                processVipOrder(order);
                            }
                        }
                        else  {
                            Order order = MLQ.getInstance().nextOrder(slowFoodOrders);
                            if(order!=null){
                                processSlowOrder(order);
                            }
                        }
                }
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private void processVipOrder(Order order) throws InterruptedException {
        int service = order.getOrderDescription().getServiceTime();
        int i = 0;
        switch (service) {
            case 15:
                while (i <= 1500000) {
                    i += 1;
                }
                break;
            case 25:
                while (i <= 2500000) {
                    i += 1;
                }
                break;
            case 10:
                while (i <= 1000000) {
                    i += 1;
                }
                break;
            case 50:
                while (i <= 500000) {
                    i += 1;
                }
                break;
            default:
                break;
        }
        this.processOrder(order);
    }

    private void processFastOrder(Order order) throws InterruptedException {
        int i = 0;
        int service = order.getOrderDescription().getServiceTime();
        if (service == 10){
            while (i <= 1000000) {
                i += 1;
            }
        }else if (service == 15){
            while (i <= 1500000) {
                i += 1;
            }
        }
        this.processOrder(order);
    }

    private void processMediumOrder(Order order) throws InterruptedException {
        int i = 0;
        int service = order.getOrderDescription().getServiceTime();
        while (i <= 2500000) {
            i += 1;
        }
        this.processOrder(order);
    }

    private void processSlowOrder(Order order) throws InterruptedException {
        int i = 0;
        int service = order.getOrderDescription().getServiceTime();
        while (i <= 5000000) {
            i += 1;
        }
        this.processOrder(order);
    }

    private void processOrder(Order order) throws InterruptedException {
        long hourNow = TClock.getInstance().getMoment();
        Statistics.getInstance().addOrderToStatistics(order,hourNow);
    }
}


