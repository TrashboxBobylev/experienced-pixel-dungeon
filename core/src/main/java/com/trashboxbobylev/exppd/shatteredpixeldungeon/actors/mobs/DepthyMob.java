package com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Badges;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.Statistics;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Speck;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Surprise;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Wound;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.missiles.CobaltScythe;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Random;

public class DepthyMob extends Mob {

    {
        state = HUNTING;
        TIME_TO_WAKE_UP = 0f;

        defenseSkill = Dungeon.hero.attackSkill(this) + Random.NormalIntRange(-10, 10);

        viewDistance = 12;

        EXP = 25 * (Dungeon.depth - 26);

        immunities.add(Terror.class);
        immunities.add(Corruption.class);
        immunities.add(Ooze.class);
    }

    public int attackSkill( Char target ) {
        return Dungeon.hero.defenseSkill(this) + Random.NormalIntRange(-10, 10);
    }

    @Override
    public void damage( int dmg, Object src ) {

        if (Dungeon.hero.lvl < 150) dmg /= 10;

        super.damage( dmg, src );
    }

    public boolean surprisedBy( Char enemy ){
        return !enemySeen && enemy == Dungeon.hero && Dungeon.hero.lvl > 200;
    }

    @Override
    public int defenseSkill( Char enemy ) {
        boolean seen = (enemySeen && (enemy.invisible == 0 && Dungeon.hero.lvl >= 200));
        if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack() || Dungeon.hero.lvl <= 200) seen = true;
        if ( seen
                && paralysed == 0
                && !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
            return this.defenseSkill;
        } else {
            return 0;
        }
    }

    @Override
    public int defenseProc( Char enemy, int damage ) {

        if (enemy instanceof Hero && (((Hero) enemy).belongings.weapon instanceof MissileWeapon || ((Hero) enemy).belongings.weapon instanceof CobaltScythe)){
            hitWithRanged = true;
        }

        if ((!enemySeen || enemy.invisible > 0 )
                && enemy == Dungeon.hero && Dungeon.hero.canSurpriseAttack() && Dungeon.hero.lvl >= 200) {
            Statistics.sneakAttacks++;
            Badges.validateRogueUnlock();
            if (enemy.buff(Preparation.class) != null) {
                Wound.hit(this);
            } else {
                Surprise.hit(this);
            }
        }

        //if attacked by something else than current target, and that thing is closer, switch targets
        if (this.enemy == null
                || (enemy != this.enemy && (Dungeon.level.distance(pos, enemy.pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
            aggro(enemy);
            target = enemy.pos;
        }

        if (buff(SoulMark.class) != null) {
            int restoration = Math.min(damage, HP);
            Dungeon.hero.buff(Hunger.class).satisfy(restoration/2);
            Dungeon.hero.HP = (int)Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP+(restoration*0.05f)));
            Dungeon.hero.sprite.emitter().burst( Speck.factory(Speck.HEALING), 1 );
        }

        return damage;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (buff(Weakness.class) != null){
            damage *= 0.95f;
        }
        return damage;
    }


}
