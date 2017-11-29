package lab2try1;

public class Lab2Try1 {

    public static void main(String[] args) {
        Integer thrQ = 100;
        MThread threads[] = new MThread[thrQ];

        for(int i = 0; i < thrQ; i++){
            threads[i] = new MThread(i);
//            try{
//                Thread.sleep(100);
//            }
//            catch(InterruptedException me){
//                
//            }
        }
        
        for(int i = 0; i < thrQ; i++){
            threads[i].start();
        }
        
        for(int i = 0; i < thrQ; i++){
            try{
                threads[i].join();
            }
            catch(InterruptedException me){
                
            }
        }
        
      //  System.out.println(MThread.meresult.get());
    }
    
}
