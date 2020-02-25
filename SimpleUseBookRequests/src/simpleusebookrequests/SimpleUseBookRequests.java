package simpleusebookrequests;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
public class SimpleUseBookRequests {
    
    static RequestBook book = new RequestBook();
    
    public static void main(String[] args) {        
        String StringFile = new Scanner(System.in).nextLine();        
        AnalizatorFile(StringFile);
        //String StringFile="u,9,1,bid\nu,11,5,ask\nq,best_bid\nu,10,2,bid\nq,best_bid\no,sell,1\nq,size,10\n";             
        /*String StringFile = new Scanner(System.in).nextLine();
        String StringFile="file.txt";       
        File file = new File(StringFile);
        AnalizatorFile(file);  */
    }

    public static boolean AnalizatorFile(String str){
        str=str.replaceAll("n", "AAAAAAAAAA");                
        String[] line=str.split("AAAAAAAAAA");        
        for(int i=0;i<line.length;i++){  
            line[i]=line[i].substring(0, line[i].length()-1);                                 
            if(line[i].startsWith("u")){                    
                int price=Integer.parseInt(line[i].substring(2, line[i].indexOf(",",2)));                      
                int size=Integer.parseInt(line[i].substring(line[i].indexOf(",",2)+1, line[i].lastIndexOf(",")));                        
                if(line[i].contains("bid")) 
                    book.u_bid(price, size);                      
                else if(line[i].contains("ask"))
                    book.u_ask(price, size);
            }
            else if(line[i].startsWith("q")){
                if(line[i].contains("best_bid"))
                    book.q_best_bid();                        
                else if(line[i].contains("best_ask"))
                    book.q_best_ask();
                else if(line[i].contains("size"))                        
                    book.q_size(Integer.parseInt(line[i].substring(line[i].lastIndexOf(",")+1, line[i].length())));                                                                    
            }
            else if(line[i].startsWith("o")){
                if(line[i].contains("buy"))
                    book.o_buy(Integer.parseInt(line[i].substring(line[i].lastIndexOf(",")+1, line[i].length())));
                else if(line[i].contains("sell"))
                    book.o_sell(Integer.parseInt(line[i].substring(line[i].lastIndexOf(",")+1, line[i].length())));
            } 
        }        
        return true;
    }
    
    public static boolean AnalizatorFile(File file){       
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {                
                if(line.startsWith("u")){
                    int price=Integer.parseInt(line.substring(2, line.indexOf(",",2)));                                               
                    int size=Integer.parseInt(line.substring(line.indexOf(",",2)+1, line.lastIndexOf(",")));                                                                                
                    if(line.contains("bid")) 
                        book.u_bid(price, size);                      
                    else if(line.contains("ask"))
                        book.u_ask(price, size);
                }
                else if(line.startsWith("q")){
                    if(line.contains("best_bid"))
                        book.q_best_bid();                        
                    else if(line.contains("best_ask"))
                        book.q_best_ask();
                    else if(line.contains("size"))                        
                        book.q_size(Integer.parseInt(line.substring(line.lastIndexOf(",")+1, line.length())));                                                                    
                }
                else if(line.startsWith("o")){
                    if(line.contains("buy"))
                        book.o_buy(Integer.parseInt(line.substring(line.lastIndexOf(",")+1, line.length())));
                    else if(line.contains("sell"))
                        book.o_sell(Integer.parseInt(line.substring(line.lastIndexOf(",")+1, line.length())));
                }                
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimpleUseBookRequests.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleUseBookRequests.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }    
}

class RequestBook{    
    protected ArrayList<Request> Type_Buy = new ArrayList<>();    
    protected ArrayList<Request> Type_Sell = new ArrayList<>();   
    
    boolean u_bid(int price, int size){
        Type_Buy.add(new Request(price, size));         
        return true;
    }
    
    boolean u_ask(int price, int size){
        Type_Sell.add(new Request(price, size));                
        return true;
    }
    
    String q_best_bid(){        
        int best_p=Type_Buy.get(0).price;        
        int best_s=Type_Buy.get(0).size;        
        for(int i=0;i<Type_Buy.size();i++)          
            if(best_p<Type_Buy.get(i).price && Type_Buy.get(i).size!=0){
                best_p=Type_Buy.get(i).price;
                best_s=Type_Buy.get(i).size;                
            }
        System.out.println(best_p+","+best_s);
        return best_p+","+best_s;        
    }
    
    String q_best_ask(){
        int best_p=Type_Sell.get(0).price;
        int best_s=Type_Sell.get(0).size;
        for(int i=0;i<Type_Sell.size();i++)          
            if(best_p>Type_Sell.get(i).price && Type_Sell.get(i).size!=0){
                best_p=Type_Sell.get(i).price;
                best_s=Type_Sell.get(i).size;
            }
        System.out.println(best_p+","+best_s);
        return best_p+","+best_s;                 
    }
    
    int q_size(int price){
        int G_size=0;
        for(int i=0;i<Type_Buy.size();i++)
            if(Type_Buy.get(i).price==price)
                G_size+=Type_Buy.get(i).size;        
        for(int i=0;i<Type_Sell.size();i++)
            if(Type_Sell.get(i).price==price)
                G_size+=Type_Sell.get(i).size;   
        System.out.println(G_size);
        return G_size;        
    }
    
    boolean o_buy(int size){         
        int index = getMinRequest_fromSell();
        if(Type_Sell.get(index).size-size>0)
            Type_Sell.set(index, new Request(Type_Sell.get(index).price, Type_Sell.get(index).size-size));
        else
            Type_Sell.remove(index);
        return true;
    }
    
    boolean o_sell(int size){
        int index = getMaxRequest_fromBuy();
        if(Type_Buy.get(index).size-size>0)
            Type_Buy.set(index, new Request(Type_Buy.get(index).price, Type_Buy.get(index).size-size));
        else
            Type_Buy.remove(index);
        return true;
    }
    
    protected int getMinRequest_fromSell(){
        int index=-1;
        int min=Type_Sell.get(0).price;
        for(int i=0;i<Type_Sell.size();i++)          
            if(min>Type_Sell.get(i).price && Type_Sell.get(i).size!=0){
                min=Type_Sell.get(i).price;
                index=i;
            }
        return index;
    }
    
    protected int getMaxRequest_fromBuy(){
        int index=-1;
        int max=Type_Buy.get(0).price;
        for(int i=0;i<Type_Buy.size();i++)          
            if(max<Type_Buy.get(i).price && Type_Buy.get(i).size!=0){
                max=Type_Buy.get(i).price;
                index=i;
            }
        return index;
    }
}

class Request{
    int price;
    int size;    
    Request(int price, int size){
        this.price=price;
        this.size=size;
    }
}
