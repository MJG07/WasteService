package garbagebot

import unibo.kotlin.planner22Util

object directionalPlanner {

    private val map = "mapRoomEmpty"
	private val toTheRight = mapOf("N" to "E", "E" to "S", "S" to "W", "W" to "N")

	private var plannedDirection = "S"

    init {
		planner22Util.initAI()
		planner22Util.loadRoomMap(map)
		// plannerUtil.showMap()
	}

    fun planFor(place: Array<String>) {
		planForGoal(place[0], place[1], place[2])
	}

    fun planForGoal(x: String, y: String, d: String) {
		planner22Util.planForGoal(x, y)
		plannedDirection = d
	}

	fun getNextPlannedMove(): String {
		var move = planner22Util.getNextPlannedMove()
		var lol = planner22Util.getDirection()
		if (move.isEmpty()) {
			val currentDirection = when (lol) {
				"upDir" -> "N"
				"downDir" -> "S"
				"leftDir" -> "W"
				"rightDir" -> "E"
				else       -> "unknownDir"
			}
			if (plannedDirection != currentDirection) {
				if (toTheRight[currentDirection] == plannedDirection) move = "r"
				else move = "l"
			}
		}
		return move
	}

	fun updateMap(move: String) {
		planner22Util.updateMap(move)
	}

	fun getX(): String {
		return planner22Util.getPosX().toString()
	}

	fun getY(): String {
		return planner22Util.getPosY().toString()
	}

	fun getD(): String {
		var lol = planner22Util.getDirection()
		return when (lol) {
			"upDir" -> "N"
			"downDir" -> "S"
			"leftDir" -> "W"
			"rightDir" -> "E"
			else       -> "unknownDir"
		}
	}
    
}