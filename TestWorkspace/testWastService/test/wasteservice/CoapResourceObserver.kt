package wasteservice;

import org.eclipse.californium.core.CoapHandler
import org.eclipse.californium.core.CoapResponse
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingDeque
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

open class CoapResourceObserver(val resourceName : String) : CoapHandler {
    
    var currentState = ""
        private set
    private val blockingDeque = ArrayBlockingQueue<String>(10)
    
    
    override fun onLoad(response: CoapResponse) {
        currentState = response.responseText
        blockingDeque.put(currentState)
        ColorsOut.outappl("CoapResourceObserver[$resourceName] state=\"$currentState\"", ColorsOut.MAGENTA)
    }

    override fun onError() {
        ColorsOut.outerr("CoapResourceObserver[$resourceName] observe error!")
    }

    fun clearHistory() {
        blockingDeque.clear()
    }

    fun getNextState() : String {
        return blockingDeque.take()
    }


}

fun connectToCoapActor(actorName: String, address: String, port : Int, ctxName : String) : Pair<CoapConnection, CoapResourceObserver> {
    val blockingQueue = ArrayBlockingQueue<Boolean>(1);
    var conn : CoapConnection? = null
    var resourceObserver : CoapResourceObserver? = null
    object : Thread() {
        override fun run() {
            try {
                conn = CoapConnection("$address:$port", "$ctxName/$actorName")
                resourceObserver = CoapResourceObserver("$ctxName:$actorName")
                conn!!.observeResource(resourceObserver!!)
                ColorsOut.outappl("connected via Coap conn:$conn", ColorsOut.CYAN)
                blockingQueue.put(true)
            } catch (e: Exception) {
                ColorsOut.outerr("connectUsingCoap ERROR:" + e.message)
            }
        }
    }.start()
    blockingQueue.take()
    return Pair(conn!!, resourceObserver!!)
}