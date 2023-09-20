package games.descent2e.components;

import core.CoreConstants;
import core.components.Counter;
import core.components.Deck;
import core.properties.Property;
import core.properties.PropertyString;
import core.properties.PropertyStringArray;
import games.descent2e.DescentGameState;
import games.descent2e.actions.Move;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static games.descent2e.DescentConstants.*;

// TODO: figure out how to do ability/heroic-feat
public class Hero extends Figure {

    Deck<DescentCard> skills;
    Deck<DescentCard> handEquipment;
    DescentCard armor;
    Deck<DescentCard> otherEquipment;
    Map<String, Integer> equipSlotsAvailable;

    // TODO: reset fatigue every quest to max fatigue

    String heroicFeat;
    boolean usedHeroAbility, featAvailable, rested, defeated;

    String ability;

    public Hero(String name, int nActionsPossible) {
        super(name, nActionsPossible);

        skills = new Deck<>("Skills", CoreConstants.VisibilityMode.VISIBLE_TO_ALL);
        handEquipment = new Deck<>("Hands", CoreConstants.VisibilityMode.VISIBLE_TO_ALL);
        otherEquipment = new Deck<>("OtherItems", CoreConstants.VisibilityMode.VISIBLE_TO_ALL);

        equipSlotsAvailable = new HashMap<>();
        equipSlotsAvailable.put("hand", 2);
        equipSlotsAvailable.put("armor", 1);
        equipSlotsAvailable.put("other", 2);

        tokenType = "Hero";
    }

    @Override
    public void resetRound() {
        super.resetRound();
    }

    protected Hero(String name, Counter actions, int ID) {
        super(name, actions, ID);
    }

    public Deck<DescentCard> getSkills() {
        return skills;
    }

    public void setSkills(Deck<DescentCard> skills) {
        this.skills = skills;
    }

    public Deck<DescentCard> getHandEquipment() {
        return handEquipment;
    }

    public void setHandEquipment(Deck<DescentCard> handEquipment) {
        this.handEquipment = handEquipment;
    }

    public DescentCard getArmor() {
        return armor;
    }

    public void setArmor(DescentCard armor) {
        this.armor = armor;
    }

    public Deck<DescentCard> getOtherEquipment() {
        return otherEquipment;
    }

    public void setOtherEquipment(Deck<DescentCard> otherEquipment) {
        this.otherEquipment = otherEquipment;
    }

    public Map<String, Integer> getEquipSlotsAvailable() {
        return equipSlotsAvailable;
    }

    public void setEquipSlotsAvailable(HashMap<String, Integer> equipSlotsAvailable) {
        this.equipSlotsAvailable = equipSlotsAvailable;
    }

    public String getHeroicFeat() {
        return heroicFeat;
    }

    public void setHeroicFeat(String heroicFeat) {
        this.heroicFeat = heroicFeat;
    }

    public boolean isFeatAvailable() {
        return featAvailable;
    }

    public void setFeatAvailable(boolean featAvailable) {
        this.featAvailable = featAvailable;
    }

    public String getAbility() {
        return ability;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public boolean hasRested() {
        return rested;
    }

    public void setRested(boolean rested) {
        this.rested = rested;
    }

    public boolean hasUsedHeroAbility() {
        return usedHeroAbility;
    }
    public void setUsedHeroAbility(boolean usedHeroAbility) {
        this.usedHeroAbility = usedHeroAbility;
    }

    public void setDefeated(DescentGameState dgs, boolean defeated) {
        this.defeated = defeated;

        if (defeated) {
            // Loses all health
            setAttribute(Figure.Attribute.Health, 0);
            // Max fatigue
            setAttributeToMax(Figure.Attribute.Fatigue);
            // Discard conditions
            getConditions().clear();
            // Remove from map
            Move.remove(dgs, this);
            setOffMap(true);
        }
        else {
            Move.replace(dgs, this);
        }
    }

    public boolean isDefeated() {
        return defeated;
    }

    public boolean equip(DescentCard c) {
        // Check if equipment
        Property cost = c.getProperty(costHash);
        if (cost != null) {
            // Equipment! Check if it's legal to equip
            String[] equip = ((PropertyStringArray) c.getProperty(equipSlotHash)).getValues();
            boolean canEquip = true;
            Map<String, Integer> equipSlots = new HashMap<>(equipSlotsAvailable);
            for (String e : equip) {
                if (equipSlots.get(e) < 1) {
                    canEquip = false;
                    break;
                } else {
                    equipSlots.put(e, equipSlots.get(e) - 1);
                }
            }
            if (canEquip) {
                equipSlotsAvailable = equipSlots;
                switch (equip[0]) {
                    case "armor":
                        armor = c;
                        break;
                    case "hand":
                        handEquipment.add(c);
                        break;
                    case "other":
                        otherEquipment.add(c);
                        break;
                }
                return true;
            }
            return false;
        } else {
            // A skill
            skills.add(c);
            return true;
        }
    }

    public List<DescentCard> getWeapons() {
        return handEquipment.stream().filter(DescentCard::isAttack).collect(Collectors.toList());
    }

    @Override
    public DicePool getAttackDice() {
        Optional<DescentCard> wpn = getWeapons().stream().findFirst();
        if (wpn.isPresent())
            return wpn.get().getDicePool();
        return DicePool.empty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hero)) return false;
        if (!super.equals(o)) return false;
        Hero hero = (Hero) o;
        return featAvailable == hero.featAvailable && rested == hero.rested &&
                Objects.equals(skills, hero.skills) && Objects.equals(handEquipment, hero.handEquipment)
                && Objects.equals(armor, hero.armor) && Objects.equals(otherEquipment, hero.otherEquipment)
                && Objects.equals(equipSlotsAvailable, hero.equipSlotsAvailable) &&
                Objects.equals(heroicFeat, hero.heroicFeat) && Objects.equals(usedHeroAbility, hero.usedHeroAbility) &&
                Objects.equals(ability, hero.ability);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), skills, handEquipment, armor, otherEquipment,
                equipSlotsAvailable, heroicFeat, usedHeroAbility, featAvailable, rested, ability);
        result = 31 * result;
        return result;
    }

    @Override
    public Hero copy() {
        Hero copy = new Hero(componentName, nActionsExecuted.copy(), componentID);
        return copyTo(copy);
    }

    @Override
    public Hero copyNewID() {
        Hero copy = new Hero(componentName, -1);
        return copyTo(copy);
    }

    @NotNull
    private Hero copyTo(Hero copy) {
        copy.equipSlotsAvailable = new HashMap<>();
        copy.equipSlotsAvailable.putAll(equipSlotsAvailable);
        copy.skills = skills.copy();
        copy.handEquipment = handEquipment.copy();
        copy.otherEquipment = otherEquipment.copy();
        if (armor != null) {
            copy.armor = armor.copy();
        }
        copy.heroicFeat = this.heroicFeat;
        copy.usedHeroAbility = this.usedHeroAbility;
        copy.featAvailable = this.featAvailable;
        copy.ability = this.ability;
        copy.rested = rested;
        super.copyComponentTo(copy);
        return copy;
    }

    /**
     * Creates a Token objects from a JSON object.
     *
     * @param figure - JSON to parse into Figure object.
     */
    protected void loadHero(JSONObject figure) {
        super.loadFigure(figure);
        String[] defence = ((PropertyStringArray) getProperty(defenceHash)).getValues();
        defenceDice = DicePool.constructDicePool(defence);
        this.featAvailable = true;
        this.heroicFeat = ((PropertyString) getProperty(heroicFeatHash)).value;
        this.ability = ((PropertyString) getProperty(abilityHash)).value;
    }

    /**
     * Loads all figures from a JSON file.
     *
     * @param filename - path to file.
     * @return - List of Figure objects.
     */
    public static List<Hero> loadHeroes(String filename) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Hero> figures = new ArrayList<>();

        try (FileReader reader = new FileReader(filename)) {

            JSONArray data = (JSONArray) jsonParser.parse(reader);
            for (Object o : data) {

                Hero newFigure = new Hero("", -1);
                newFigure.loadHero((JSONObject) o);
                figures.add(newFigure);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return figures;
    }
}