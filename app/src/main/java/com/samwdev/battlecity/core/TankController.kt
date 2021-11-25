package com.samwdev.battlecity.core

class TankController(
    private val tankState: TankState,
    private val bulletState: BulletState, // todo move out?
    private val handheldControllerState: HandheldControllerState,
) : TickListener {
    private val tankId: TankId get() = tankState.playerTankId

    override fun onTick(tick: Tick) {
        val tank = tankState.getTankOrNull(tankId) ?: return
        if (tank.isSpawning) {
            return
        }
        val distance = tank.maxSpeed * tick.delta

        val newDir = handheldControllerState.direction
        if (newDir != null) {
            tankState.moveTank(tankId, newDir)
        } else {
            tankState.stopTank(tankId)
        }

        if (handheldControllerState.firePressed && tank.remainingCooldown <= 0) {
            if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
                bulletState.fire(tank)
                tankState.startFireCooldown(tankId)
            }
        }
    }
}