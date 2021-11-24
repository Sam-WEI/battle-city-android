package com.samwdev.battlecity.core

class BotTankController(
    private val tankState: TankState,
    private val tankId: TankId,
    private val bulletState: BulletState, // todo move out?
) : TickListener {
    private val botAi: BotAi = BotAi()

    override fun onTick(tick: Tick) {
        if (!tankState.isTankAlive(tankId)) {
            return
        }
        botAi.onTick(tick)
        val tank = tankState.getTank(tankId)
        if (tank.isSpawning) {
            return
        }

        val command = botAi.getCommand()
        val distance = tank.maxSpeed * tick.delta

        when (command) {
            is Fire -> {
                if (tank.remainingCooldown <= 0) {
                    if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
                        bulletState.fire(tank)
                        tankState.startCooldown(tank.id)
                    }
                }
            }
            is Stop -> {
                tankState.stopTank(tank.id)
            }
            is Turn -> {
                tankState.moveTank(tank.id, command.direction)
            }
            is Proceed -> {
                tankState.moveTank(tank.id, tank.movingDirection)
            }
        }
    }
}