package wasteservice

import it.unibo.ctxwastservice.main
import it.unibo.kactor.QakContext.Companion.getActor
import unibo.comm22.coap.CoapConnection
import unibo.comm22.utils.CommSystemConfig
import it.unibo.kactor.ActorBasic
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommUtils
import wasteservice.ConnTcp
import java.lang.Exception

class TestTransportTrolley {

    lateinit var coapConnRes : Pair<CoapConnection, CoapResourceObserver>
    lateinit var coapObs : CoapResourceObserver
    var setupOk = false

    private val conn: CoapConnection? = null
    @Before
    fun up() {
        if(!setupOk) {
            CommSystemConfig.tracing = false
            object : Thread() {
                override fun run() {
                    main()
                }
            }.start()
            waitForApplStarted()
            coapConnRes = connectToCoapActor("transporttrolley", "localhost", 8032, "ctxwastservice")
            coapObs = coapConnRes.second
            coapObs.getNextState()
            setupOk=true
        }
    }

    protected fun waitForApplStarted() {
        var trolley = getActor("transporttrolley")
        while (trolley == null) {
            ColorsOut.outappl("TestCore0 waits for appl ... ", ColorsOut.GREEN)
            CommUtils.delay(200)
            trolley = getActor("transporttrolley")
        }
    }

    @After
    fun down() {
        ColorsOut.outappl("TestCore0 ENDS", ColorsOut.BLUE)
    }

    @Test
    fun testTrolleyok() {
        ColorsOut.outappl("testLoadok STARTS", ColorsOut.BLUE)
        var TrolleyRequestStr = "msg(pickup,request,python,transporttrolley,pickup(_),1)"
        try {
            val connTcp = ConnTcp("localhost", 8032)
            var answer = connTcp.request(TrolleyRequestStr)
            ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN)
            Assert.assertTrue(answer.contains("executeaction(donePickup)"))

            TrolleyRequestStr = "msg(deposita,request,python,transporttrolley,deposita(GLASS),3)"
            answer = connTcp.request(TrolleyRequestStr)
            ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN)
            Assert.assertTrue(answer.contains("executeaction(doneDeposit)"))

            TrolleyRequestStr = "msg(pickup,request,python,transporttrolley,pickup(_),5)"
            answer = connTcp.request(TrolleyRequestStr)
            ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN)
            Assert.assertTrue(answer.contains("executeaction(donePickup)"))

            TrolleyRequestStr = "msg(deposita,request,python,transporttrolley,deposita(PLASTIC),7)"
            answer = connTcp.request(TrolleyRequestStr)
            ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN)
            Assert.assertTrue(answer.contains("executeaction(doneDeposit)"))
            
            TrolleyRequestStr = "msg(home,request,python,transporttrolley,home(_),9)"
            answer = connTcp.request(TrolleyRequestStr)
            ColorsOut.outappl("testTrolley answer=$answer", ColorsOut.GREEN)
            Assert.assertTrue(answer.contains("executeaction(doneHome)"))

            connTcp.close()
        } catch (e: Exception) {
            ColorsOut.outerr("testLoadok ERROR:" + e.message)
        }
    }


}