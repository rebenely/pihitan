package xyz.ravencrows.pihitan.navigator;

import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.robot.Robot;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ravencrows.pihitan.input.PihitanAction;
import xyz.ravencrows.pihitan.templates.ItemPosition;
import xyz.ravencrows.pihitan.templates.ItemType;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.templates.TemplateItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class responsible for moving the mouse on screen
 */
public class ScreenNavigator {
  private static final Logger logger = LoggerFactory.getLogger(ScreenNavigator.class);
  public static final Duration POST_STEP_DELAY = Duration.millis(100);

  final private List<NavigatorSection> sections;
  final private int sectionsSize;
  final private NavigatorPos prevPreset;
  final private NavigatorPos nextPreset;


  // selected items
  private Integer currSection;
  private Integer currItem;

  // this is only updated when section is pressed
  private Integer selectedSection;

  private final Rectangle2D externalAppBounds;

  private final Robot robot;

  public ScreenNavigator(Template template, Rectangle2D externalAppBounds, Robot robot) {
    this.sections = new ArrayList<>();
    this.externalAppBounds = externalAppBounds;
    this.robot = robot;

    init(template);
    this.sectionsSize = sections.size();

    prevPreset = NavigatorPos.fromItem(template.getPrevPreset(), externalAppBounds);
    nextPreset = NavigatorPos.fromItem(template.getNextPreset(), externalAppBounds);
  }

  /**
   * Build sections and items
   * Probably need to rework to allow sections in sections
   * Current workaround is preStep used in amp selector
   */
  private void init(Template template) {
    currSection = 0;
    currItem = 0;
    selectedSection = 0;

    // get template sections
    template.getSections().forEach(section -> {
      final NavigatorPos currPos = NavigatorPos.fromItem(section.getPos(), externalAppBounds);
      final NavigatorSection sec = new NavigatorSection(section.getId(), currPos);

      final ItemPosition postStep = section.getPostStep();
      if (postStep != null) {
        sec.setPostStep(NavigatorPos.fromItem(section.getPostStep(), externalAppBounds));
      }

      // get items and flatten
      final List<NavigatorItem> flattenedItems = new ArrayList<>(buildItem(section.getItems(), currPos, section.getId()));
      sec.setItems(flattenedItems);

      sections.add(sec);
    });
  }

  /**
   * Flatten items, depth first, I think
   */
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

  public void recomputeAppBounds(Rectangle2D newAppBounds) {
    sections.forEach(section -> {
      section.getPos().recompute(newAppBounds);
      Optional.ofNullable(section.getPostStep()).ifPresent(postStep -> postStep.recompute(newAppBounds));
      section.getItems().forEach(item -> {
        item.getPressPos().recompute(newAppBounds);
        item.getPos().recompute(newAppBounds);
      });
    });
  }

  /**
   * Main public navigation method, called in the listeners
   */
  public String navigate(PihitanAction pihitanAction, Scene scene) {
    logger.info("Received action {}", pihitanAction);
    final String activeNavItem = currItem != null
            ? getCurrentItem().getDisplayName()
            : sections.get(currSection).getDisplayName();
    return switch (pihitanAction) {
      case KNOB_LEFT:
        this.turnKnobLeft();
        yield activeNavItem;
      case KNOB_RIGHT:
        this.turnKnobRight();
        yield activeNavItem;
      case PREV_SECTION:
        yield this.moveToPreviousSection().getDisplayName();
      case NEXT_SECTION:
        yield this.moveToNextSection().getDisplayName();
      case PRESS:
        this.press(scene);
        yield activeNavItem;
      case PREV_ITEM:
        yield this.moveToPreviousItem().getDisplayName();
      case NEXT_ITEM:
        yield this.moveToNextItem().getDisplayName();
      case PREV_PRESET:
        this.pressPreset(prevPreset, scene);
        yield "Prev preset";
      case NEXT_PRESET:
        this.pressPreset(nextPreset, scene);
        yield "Next preset";
    };
  }

  /* Private methods below are for navigation */

  private NavigatorSection moveToNextSection() {
    currSection = Math.floorMod(currSection + 1, sectionsSize);
    moveSection();
    return sections.get(currSection);
  }

  private NavigatorSection moveToPreviousSection() {
    currSection = Math.floorMod(currSection - 1, sectionsSize);
    moveSection();
    return sections.get(currSection);
  }

  private void moveSection() {
    currItem = null;
    final NavigatorSection navSection = sections.get(currSection);
    robot.mouseMove(navSection.getPos().getX(), navSection.getPos().getY());
  }

  private NavigatorItem moveToNextItem () {
    final int size = sections.get(selectedSection).getItems().size();
    currItem = currItem == null ? 0 : Math.floorMod(currItem + 1, size);
    moveItems();
    return getCurrentItem();
  }

  private NavigatorItem moveToPreviousItem () {
    final int size = sections.get(selectedSection).getItems().size();
    currItem = currItem == null ? 0 : Math.floorMod(currItem - 1, size);
    moveItems();
    return getCurrentItem();
  }

  private void moveItems() {
    final NavigatorItem item = getCurrentItem();
    robot.mouseMove(item.getPos().getX(), item.getPos().getY());
  }

  private void press(Scene scene) {
    // this means user is currently navigating through sections
    if(currItem == null) {
      final NavigatorSection section = sections.get(currSection);
      final NavigatorPos postStep = sections.get(currSection).getPostStep();

      // press mouse on the current loc
      robot.mousePress(MouseButton.PRIMARY);

      // request focus so listener events will still work after
      scene.getWindow().requestFocus();

      // update section
      selectedSection = currSection;

      if (postStep != null) {
        PauseTransition pause = doPostStep(scene, postStep, section);
        pause.play();
      }
      return;
    }

    // User navigating through items

    // handle press of parent in items
    NavigatorItem item = getCurrentItem();
    robot.mouseMove(item.getPressPos().getPoint());
    robot.mousePress(MouseButton.PRIMARY);

    // move back to original pos
    robot.mouseMove(item.getPos().getPoint());

    // request focus so listener events will still work after
    scene.getWindow().requestFocus();
  }

  private PauseTransition doPostStep(Scene scene, NavigatorPos postStep, NavigatorSection section) {
    PauseTransition pause = new PauseTransition(POST_STEP_DELAY);
    PauseTransition press = new PauseTransition(POST_STEP_DELAY);
    PauseTransition moveBack = new PauseTransition(POST_STEP_DELAY);
    moveBack.setOnFinished(event -> {
      // move mouse back
      robot.mouseMove(section.getPos().getX(), section.getPos().getY());
    });
    press.setOnFinished(event -> {
      // delay for a bit so the program can be clicked
      // might be due to the distance it travels
      // pressing effects doesn't require this
      robot.mousePress(MouseButton.PRIMARY);

      // request focus so listener events will still work after
      scene.getWindow().requestFocus();

      moveBack.play();
    });
    pause.setOnFinished(event -> {
      robot.mouseMove(postStep.getX(), postStep.getY());
      press.play();
    });
    return pause;
  }

  private NavigatorItem getCurrentItem() {
    return sections.get(selectedSection).getItems().get(currItem);
  }

  private void turnKnobLeft() {
    robot.mouseWheel(1);
  }

  private void turnKnobRight() {
    robot.mouseWheel(-1);
  }

  private void pressPreset(NavigatorPos position, Scene scene) {
    // store current mouse position
    Point2D currMousePos = robot.getMousePosition();

    // move to preset loc and press
    robot.mouseMove(position.getPoint());
    robot.mousePress(MouseButton.PRIMARY);

    // move back to previous loc
    robot.mouseMove(currMousePos);

    // request focus so listener events will still work after
    scene.getWindow().requestFocus();
  }

  public Robot getRobot() {
    return robot;
  }
}
