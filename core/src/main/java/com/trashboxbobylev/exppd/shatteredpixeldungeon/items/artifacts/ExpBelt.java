package com.trashboxbobylev.exppd.shatteredpixeldungeon.items.artifacts;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;

public class ExpBelt extends Artifact {
    private static final int BELT_UPGRADE_SCALE = 50;

    {
        image = ItemSpriteSheet.ARTIFACT_BELT;

        levelCap = 10;
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped( Dungeon.hero )){
            desc += "\n\n";
            if (cursed)
                desc += Messages.get(this, "desc_cursed");
            else
                desc += Messages.get(this, "desc_equipped", level()*BELT_UPGRADE_SCALE - exp);
        }
        return desc;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new ExpObtain();
    }

    public class ExpObtain extends ArtifactBuff {
        public void obtain(int expObt){
            exp += expObt;
            if (exp >= level()*BELT_UPGRADE_SCALE){
                while (exp >= level()*BELT_UPGRADE_SCALE) {
                    upgrade();
                    exp -= level()*BELT_UPGRADE_SCALE;
                }
                GLog.p(Messages.get(this, "levelup"));
            }
        }
    }
}
