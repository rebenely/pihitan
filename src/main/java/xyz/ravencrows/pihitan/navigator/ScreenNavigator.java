package xyz.ravencrows.pihitan.navigator;

import com.google.gson.Gson;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import xyz.ravencrows.pihitan.PihitanApp;
import xyz.ravencrows.pihitan.input.PihitanAction;
import xyz.ravencrows.pihitan.templates.ItemPosition;
import xyz.ravencrows.pihitan.templates.ItemType;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.templates.TemplateItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class responsible for moving the mouse on screen
 */
public class ScreenNavigator {
  final private List<NavigatorSection> sections;
  final private int sectionsSize;
  private List<NavigatorItem> items; //reference for sections.items

  // hovered items
  private NavigatorSection hoverSection;
  private NavigatorItem hoverItem;

  // selected items
  private Integer currSection;
  private Integer currItem;

  // this is only updated when section is pressed
  private Integer selectedSection;

  private final Rectangle2D externalAppBounds;

  private final Robot robot;

  public ScreenNavigator(Template template, Rectangle2D externalAppBounds) {
    this.sections = new ArrayList<>();
    this.externalAppBounds = externalAppBounds;
    this.robot = new Robot();
    init(template);
    this.sectionsSize = sections.size();

    Gson gson = new Gson();
    System.out.println(gson.toJson(sections));
  }

  private void init(Template template) {
    currSection = 0;
    currItem = 0;
    selectedSection = 0;

    // get template sections
    template.getSections().forEach(section -> {
      final NavigatorPos currPos = NavigatorPos.fromItem(section.getPos(), externalAppBounds);
      final NavigatorSection sec = new NavigatorSection(section.getId(), currPos);

      final ItemPosition preStep = section.getPreStep();
      if (preStep != null) {
        sec.setPreStep(NavigatorPos.fromItem(section.getPreStep(), externalAppBounds));
      }

      // get items and flatten
      final List<NavigatorItem> flattenedItems = new ArrayList<>(buildItem(section.getItems(), currPos, section.getId()));
      sec.setItems(flattenedItems);

      sections.add(sec);
    });
  }

  private List<NavigatorItem> buildItem(List<TemplateItem> templateItems, NavigatorPos pressPos, String parentName) {
    if(templateItems == null) {
      return Collections.emptyList();
    }
    final List<NavigatorItem> flattenedItems = new ArrayList<>();

    templateItems.forEach(templateItem -> {
      final ItemType type = templateItem.getType();
      final NavigatorItem newItem = new NavigatorItem(templateItem, externalAppBounds);
      final boolean isGear = ItemType.GEAR.equals(type);

      newItem.setPressPos(ItemType.TOGGLE.equals(type) ? newItem.getPos() : pressPos);
      newItem.setParent(parentName);

      if(!isGear) {
        flattenedItems.add(newItem);
      }

      // recurse deep if GEAR
      if (ItemType.GEAR.equals(type)) {
        flattenedItems.addAll(buildItem(templateItem.getItems(), NavigatorPos.fromItem(templateItem.getPos(), externalAppBounds), templateItem.getId()));
      }
    });

    return flattenedItems;
  }

  public NavigatorSection moveToNextSection() {
    currSection = currSection == null ? 0 : Math.floorMod(currSection + 1, sectionsSize);
    moveSection();
    return sections.get(currSection);
  }

  public NavigatorSection moveToPreviousSection() {
    currSection = currSection == null ? 0 : Math.floorMod(currSection - 1, sectionsSize);
    moveSection();
    return sections.get(currSection);
  }

  private void moveSection() {
    currItem = null;
    final NavigatorSection navSection = sections.get(currSection);
    robot.mouseMove(navSection.getPos().getX(), navSection.getPos().getY());
  }

  public NavigatorItem moveToNextItem () {
    final int size = sections.get(selectedSection).getItems().size();
    currItem = currItem == null ? 0 : Math.floorMod(currItem + 1, size);
    moveItems();
    return getCurrentItem();
  }

  public NavigatorItem moveToPreviousItem () {
    final int size = sections.get(selectedSection).getItems().size();
    currItem = currItem == null ? 0 : Math.floorMod(currItem - 1, size);
    moveItems();
    return getCurrentItem();
  }

  private void moveItems() {
    final NavigatorItem item = getCurrentItem();
    robot.mouseMove(item.getPos().getX(), item.getPos().getY());
  }

  public void press(Scene scene) {
    // this means user is currently navigating through sections
    if(currItem == null) {
      final NavigatorSection section = sections.get(currSection);
      final NavigatorPos preStep = sections.get(currSection).getPreStep();
      if (preStep != null) {
        robot.mouseMove(preStep.getX(), preStep.getY());
        robot.mousePress(MouseButton.PRIMARY);

        // move mouse back
        robot.mouseMove(section.getPos().getX(), section.getPos().getY());
      }

      // press mouse on the current loc
      robot.mousePress(MouseButton.PRIMARY);
      // update section
      selectedSection = currSection;

      // request focus so listener events will still work after
      scene.getWindow().requestFocus();
      return;
    }

    // handle press of parent in items
    NavigatorItem item = getCurrentItem();
    robot.mouseMove(item.getPressPos().getX(), item.getPressPos().getY());
    robot.mousePress(MouseButton.PRIMARY);

    // move back to original pos
    robot.mouseMove(item.getPos().getX(), item.getPos().getY());

    // request focus so listener events will still work after
    scene.getWindow().requestFocus();
  }

  public NavigatorItem getCurrentItem() {
    return sections.get(selectedSection).getItems().get(currItem);
  }

  public void turnKnobLeft() {
    robot.mouseWheel(1);
  }

  public void turnKnobRight() {
    robot.mouseWheel(-1);
  }

  public void navigate(PihitanAction pihitanAction, Scene scene) {
    System.out.println(pihitanAction);
    switch (pihitanAction) {
      case KNOB_LEFT:
      this.turnKnobLeft();
      break;
      case KNOB_RIGHT:
        this.turnKnobRight();
        break;
      case PREV_SECTION:
        this.moveToPreviousSection();
        break;
      case NEXT_SECTION:
        this.moveToNextSection();
        break;
      case PRESS:
        this.press(scene);
        break;
      case PREV_ITEM:
        this.moveToPreviousItem();
        break;
      case NEXT_ITEM:
        this.moveToNextItem();
        break;
    }
  }
}
