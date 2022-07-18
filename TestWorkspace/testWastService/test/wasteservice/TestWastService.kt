package wasteservice

import it.unibo.ctxwastservice.main
import it.unibo.kactor.QakContext.Companion.getActor
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import unibo.comm22.utils.CommUtils
import wasteservice.state.Material
import wasteservice.state.WasteState

class TestWastService {

    lateinit var coapConnRes : Pair<CoapConnection, CoapResourceObserver>
    lateinit var coapObs : CoapResourceObserver
    lateinit var currentState : WasteState
    var setupOk = false

    @Before
    fun setup() {
        if(!setupOk) {
            ColorsOut.outappl("setting up...", ColorsOut.YELLOW)
            CommSystemConfig.tracing = false
            object : Thread() {
                override fun run() {
                    main()
                }
            }.start()
            ColorsOut.outappl("waiting for context started...", ColorsOut.YELLOW)
            waitForApplStarted()
            ColorsOut.outappl("context started", ColorsOut.GREEN)
            ColorsOut.outappl("setting up coap...", ColorsOut.YELLOW)
            coapConnRes = connectToCoapActor("wastservice", "localhost", 8032, "ctxwastservice")
            coapObs = coapConnRes.second
            ColorsOut.outappl("coap started", ColorsOut.GREEN)
            currentState = WasteState.fromStringRepresentation(coapObs.getNextState())
            setupOk = true
        }
    }

    protected fun waitForApplStarted() {
        var wastservice = getActor("wastservice")
        while (wastservice == null) {
            ColorsOut.outappl("TestCore0 waits for appl ... ", ColorsOut.GREEN)
            CommUtils.delay(200)
            wastservice = getActor("wastservice")
        }
    }

    @After
    fun down() {
        ColorsOut.outappl("Test end", ColorsOut.BLUE)
        currentState = WasteState.fromStringRepresentation(coapObs.currentState)
        coapObs.clearHistory()
    }

    @Test
    fun testLoadok() {
        val currentLoad = WasteState.fromStringRepresentation(coapObs.currentState).getLoadOf(Material.GLASS)
        val truckload= 21.0
        ColorsOut.outappl("testLoadok STARTS", ColorsOut.BLUE)
        val truckRequestStr = "msg(deposit,request,python,wastservice,deposit($truckload,glass),1)"
        try {
            val connTcp = ConnTcp("localhost", 8032)
            val answer = connTcp.request(truckRequestStr)
            ColorsOut.outappl("testLoadok answer=$answer", ColorsOut.GREEN)
            connTcp.close()
            assertTrue(answer.contains("loadaccept"))
            assertEquals( currentLoad.toDouble() + truckload.toDouble() , WasteState.fromStringRepresentation(coapObs.getNextState()).getLoadOf(Material.GLASS),)

        } catch (e: Exception) {
            ColorsOut.outerr("testLoadok ERROR:" + e.message)
        }
    }

    @Test
    fun testLoadno(){
        val currentLoad = WasteState.fromStringRepresentation(coapObs.currentState).getLoadOf(Material.GLASS)
        ColorsOut.outappl("testLoadno START", ColorsOut.GREEN)
        val truckRequestStr = "msg(deposit,request,python,wastservice,deposit(23,glass),1)"
        try {
            val connTcp = ConnTcp("localhost", 8032)
            val answer = connTcp.request(truckRequestStr)
            ColorsOut.outappl("testLoadno answer=$answer", ColorsOut.RED)
            connTcp.close()
            assertTrue(answer.contains("loadreject"))
            assertEquals( currentLoad, WasteState.fromStringRepresentation(coapObs.getNextState()).getLoadOf(Material.GLASS))

        } catch (e: Exception) {
            ColorsOut.outerr("testLoadok ERROR:" + e.message)
        }
    }

}