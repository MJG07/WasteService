/* Generated by AN DISI Unibo */ 
package it.unibo.transporttrolley

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Transporttrolley ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "idle"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
					lateinit var TrolleyPos : String
		return { //this:ActionBasciFsm
				state("idle") { //this:State
					action { //it:State
						 TrolleyPos = "home"  
						updateResourceRep( "trolleyPos(home)"  
						)
					}
					 transition(edgeName="t01",targetState="pickup",cond=whenRequest("pickup"))
				}	 
				state("pickup") { //this:State
					action { //it:State
						 TrolleyPos = "Indoor"  
						updateResourceRep( "trolleyPos(indoor)"  
						)
						answer("pickup", "executeaction", "executeaction(donePickup)"   )  
					}
					 transition(edgeName="t02",targetState="deposit",cond=whenRequest("deposita"))
				}	 
				state("deposit") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("deposita(BOX)"), Term.createTerm("deposita(BOX)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
									var box = payloadArg(0)	 
								println(box)
								if(  box.trim().uppercase()=="glass"  
								 ){ TrolleyPos = "glassBox"  
								updateResourceRep( "trolleyPos(glassBox)"  
								)
								}
								else
								 { TrolleyPos = "plasticBox"  
								 updateResourceRep( "trolleyPos(plasticBox)"  
								 )
								 }
						}
						answer("deposita", "executeaction", "executeaction(doneDeposit)"   )  
					}
					 transition(edgeName="t03",targetState="pickup",cond=whenRequest("pickup"))
					transition(edgeName="t04",targetState="home",cond=whenRequest("home"))
				}	 
				state("home") { //this:State
					action { //it:State
						answer("home", "executeaction", "executeaction(doneHome)"   )  
					}
					 transition( edgeName="goto",targetState="idle", cond=doswitch() )
				}	 
			}
		}
}
