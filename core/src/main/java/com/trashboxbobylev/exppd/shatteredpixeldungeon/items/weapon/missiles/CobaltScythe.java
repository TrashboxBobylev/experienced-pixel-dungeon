package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.missiles;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Actor;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Item;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.bags.Bag;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.enchantments.Swift;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee.T6Weapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.MissileSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class CobaltScythe extends MeleeWeapon {
    {
        image = ItemSpriteSheet.COBALT_SCYTHE;

        stackable = false;

        tier = 6;

        levelKnown = true;

        bones = true;

        defaultAction = AC_THROW;
        usesTargeting = true;
    }

    @Override
    public int min() {
        return Math.max(0, min( level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
    }

    @Override
    public int min(int lvl) {
        return  6 +
                lvl;
    }

    @Override
    public int max() {
        return Math.max(0, max( level() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) ));
    }

    @Override
    public int max(int lvl) {
        return  tier*7 +     //49
                12 * lvl;//+12
    }

    public int exp;

    @Override
    public Item upgrade() {
        return upgrade(true);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("exp", exp);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        exp = bundle.getInt("exp");
    }

    public void gainExp(int exp ) {
        this.exp += exp;
        if (this.exp >= 350*level()){
            while (this.exp >= 350*level()){
                this.exp -= 350*level();
                upgrade();
                updateQuickslot();
            }
        }
    }

    public boolean holster = false;

    @Override
    public boolean collect(Bag container) {
        if (container instanceof MagicalHolster) holster = true;
        return super.collect(container);
    }

    @Override
    public int throwPos(Hero user, int dst) {
        if (hasEnchant(Projecting.class, user)
                && !Dungeon.level.solid[dst] && Dungeon.level.distance(user.pos, dst) <= 4){
            return dst;
        } else {
            return super.throwPos(user, dst);
        }
    }

    @Override
    protected void onThrow( int cell ) {
        Char enemy = Actor.findChar( cell );
        if (enemy == null || enemy == curUser) {
            super.onThrow( cell );
        } else {
            if (!curUser.shoot( enemy, this )) {
                rangedMiss( cell );
            } else {

                rangedHit( enemy, cell );

            }
        }
    }


    protected float durabilityPerUse() {
        return 0;
    }

    @Override
    public float castDelay(Char user, int dst) {
        if (Actor.findChar( dst ) != null
                && user.buff(Swift.SwiftAttack.class) != null
                && user.buff(Swift.SwiftAttack.class).boostsRanged()) {
            user.buff(Swift.SwiftAttack.class).detach();
            return 0;
        }
        return speedFactor( user );
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public String info() {

        String info = desc();

        info += "\n\n" + Messages.get( MissileWeapon.class, "stats",
                tier,
                Math.round(augment.damageFactor(min())),
                Math.round(augment.damageFactor(max())),
                STRReq());

        if (STRReq() > Dungeon.hero.STR()) {
            info += " " + Messages.get(Weapon.class, "too_heavy");
        } else if (Dungeon.hero.STR() > STRReq()){
            info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
        }

        if (enchantment != null && (cursedKnown || !enchantment.curse())){
            info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
            info += " " + Messages.get(enchantment, "desc");
        }

        if (cursed && isEquipped( Dungeon.hero )) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
        } else if (cursedKnown && cursed) {
            info += "\n\n" + Messages.get(Weapon.class, "cursed");
        } else if (!isIdentified() && cursedKnown){
            info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
        }

        info += "\n\n" + Messages.get(MissileWeapon.class, "distance");

        info += " " + Messages.get(T6Weapon.class, "exp", 350*level() - exp);

        switch (augment) {
            case SPEED:
                info += "\n\n" + Messages.get(Weapon.class, "faster");
                break;
            case DAMAGE:
                info += "\n\n" + Messages.get(Weapon.class, "stronger");
                break;
            case NONE:
        }

        return info;
    }

    @Override
    public int price() {
        return 6 * tier * quantity * (level() + 1);
    }



    public void rangedHit(Char enemy, int cell ) {
        circleBack(cell, curUser);

        //throws other chars around the center.
        for (int i  : PathFinder.NEIGHBOURS8){
            Char ch = Actor.findChar(cell + i);

            if (ch != null && ch != curUser){
                ch.damage(Math.round(damageRoll(curUser)), this);
            }
        }
    }

    private boolean throwEquiped;

    protected void rangedMiss( int cell ) {
        circleBack( cell, curUser );
    }

    private void circleBack( int from, Hero owner ) {

        ((MissileSprite)curUser.sprite.parent.recycle( MissileSprite.class )).
                reset( from, owner.sprite, curItem, null );

        if (throwEquiped) {
            owner.belongings.weapon = this;
            owner.spend( -TIME_TO_EQUIP );
            Dungeon.quickslot.replacePlaceholder(this);
            updateQuickslot();
        } else
        if (!collect( curUser.belongings.backpack )) {
            Dungeon.level.drop( this, owner.pos ).sprite.drop();
        }
    }

    @Override
    public void cast( Hero user, int dst ) {
        throwEquiped = isEquipped( user ) && !cursed;
        if (throwEquiped) Dungeon.quickslot.convertToPlaceholder(this);
        super.cast( user, dst );
    }

    @Override
    public Item upgrade(boolean enchant ) {
        super.upgrade( enchant );

        updateQuickslot();

        return this;
    }


}
