package com.samwdev.battlecity.core

class TankController(
    private val tankState: TankState,
    private val bulletState: BulletState,
    private val handheldControllerState: HandheldControllerState,
) : TickListener() {
    private val tankId: TankId get() = tankState.playerTankId

    override fun onTick(tick: Tick) {
        val tank = tankState.getTankOrNull(tankId) ?: return
        if (tank.isSpawning) {
            return
        }
        val dirsInLastTick = handheldControllerState.consumeSteerInput()
        if (dirsInLastTick.isNotEmpty()) {
            dirsInLastTick.forEach {
                tankState.moveTank(tankId, it)
            }
        } else {
            tankState.stopTank(tankId)
        }

        if (handheldControllerState.consumeFire()) {
            bulletState.fire(tank)
        }
    }
}