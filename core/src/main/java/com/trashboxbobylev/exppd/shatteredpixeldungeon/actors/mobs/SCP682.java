package com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Corruption;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Amulet;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.HeroSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SCP682 extends Mob {

    public int numberHits;

    {
        spriteClass = HeroSprite.class;

        HP = HT = Integer.MAX_VALUE;
        defenseSkill = Dungeon.hero.defenseSkill(this);
        EXP = 1000000;

        numberHits = 500;
        baseSpeed = 20f;

        loot = new Amulet();
        lootChance = 1f;
        immunities.add(Corruption.class);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("hits", numberHits);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        numberHits = bundle.getInt("hits");
    }

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.01f;
    }

    @Override
    public int damageRoll() {
        return Random.IntRange( Integer.MIN_VALUE, Integer.MAX_VALUE );
    }

    @Override
    public int attackSkill( Char target ) {
        return 18000000;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (numberHits > 0){
            numberHits--;
            dmg = 0;
        }

        super.damage(dmg, src);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(200, 500);
    }
}
