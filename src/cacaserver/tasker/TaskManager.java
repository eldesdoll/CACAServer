package cacaserver.tasker;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase se encarga de instanciar
 * todos los hilos posibles que tendremos
 * en el servidor, aún no tienen definidas, 
 * solo instancia los hilos y tareas que van a ser 
 * tomadas
 * @author ivan_
 */
public class TaskManager 
{
    /**
     * @param threads Lista de hilos abiertos
     * @param pending Lista de tareas pendientes
     * @param MIN_THREADS Mínimo de threads disponibles para correr
     * @param logger Registra las excepciones ocurridas
     */
    private static ArrayList<Thread> threads;
    private final static LinkedBlockingQueue<Task> pending;
    private static int MIN_THREADS = 8;
    private static Logger logger;

    
    /**
     * Constructor estático.
     * Se inicializan los ArrayLists de threads y de
     * pendientes
     * Se obtienen la cantidad de procesos que se van a 
     * ejecutar basados en los procesadores que se tienen
     * en el equipo
     * Por cada thread se crea un hilo que verificará si
     * hay tareas pendientes en la cola y las ejecutará
     */
    static
    {
        threads = new ArrayList<>();
        pending = new LinkedBlockingQueue<>();
        logger = Logger.getLogger("TaskManager");
        int processors = Runtime.getRuntime().availableProcessors();
        int totalThreads = (processors<MIN_THREADS)? MIN_THREADS : processors*2;
        for (int i = 0; i < totalThreads; i++)  
        {
            Thread thread = new Thread(()->
                {
                    while(true)
                    {
                        Task task = null;
                        try 
                        {
                            task = pending.take();
                        } 
                        catch (InterruptedException ex) 
                        {
                            logger.log(Level.SEVERE,ex.getMessage());
                        }
                        if(task!=null)
                        {
                            task.execute();
                        }
                    }
                });
            threads.add(thread);
            thread.start();
        }
    }
    
    /**
     * Encola tareas para que los threads
     * la tomen ASAP
     * @param t task a encolar
     */
    public static void equeue(Task t)
    {
        try 
        {
            pending.put(t);
        } 
        catch (InterruptedException ex) 
        {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }
    
}
