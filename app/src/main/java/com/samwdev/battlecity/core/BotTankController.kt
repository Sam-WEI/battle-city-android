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
        val command = botAi.getCommand()
        val move = tank.speed * tick.delta
        var newX = tank.x
        var newY = tank.y
        var newDir = tank.direction
        val newTank = tank.copy(x = newX, y = newY, direction = newDir)

        tankState.updateTank(tank.id, newTank)

        when (command) {
            is Fire -> {
                if (tank.remainingCooldown <= 0) {
                    if (bulletState.countBulletForTank(tank.id) < tank.maxBulletCount) {
                        bulletState.fire(newTank)
                        tankState.startCooldown(newTank.id)
                    }
                }
            }
            is Stop -> {}
            is Turn -> {
                tankState.updateTank(tank.id, tank.copy(direction = command.direction))
            }
            is Proceed -> {
                tankState.updateTank(tank.id, tank.move(tank.speed * tick.delta))
            }
        }
    }
}