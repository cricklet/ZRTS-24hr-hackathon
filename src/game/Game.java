package game;

import inputevents.AddEntity;
import inputevents.CharacterPressedEvent;
import inputevents.DelayedEntity;
import inputevents.GatherResource;
import inputevents.GlobalEvent;
import inputevents.RightClickEvent;
import inputevents.SelectEvent;
import inputevents.SelectPointEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

public class Game extends JFrame implements KeyListener {

	public static final int WORLD_WIDTH = 1280;
	public static final int WORLD_HEIGHT = 960;

	public static final int STARTING_FOOD = 10000;
	public static final int STARTING_MINERALS = 10000;

	public static final int NORMAL_MODE = 0;
	public static final int BUILD_MODE = 1;
	public static final int DEBUG_MODE = 2;
	public int mode;

	public static final int ADD_ZOMBIE = 0;
	public static final int ADD_TOWER = 1;
	public static final int ADD_VILLAGER = 2;
	public int addMode;

	public DrawingPanel drawing;

	/* PLAYER VALUES */
	public int[] resources = { 0, STARTING_FOOD, STARTING_MINERALS };
	public int score;

	public Game(String name) {
		super(name);
		setResizable(false);
		mode = NORMAL_MODE;
	}

	private static void createAndShowGUI() {
		Game frame = new Game("Game");

		frame.initFrame();

		frame.initGame();
		frame.startGame();
	}

	private JPanel side_panel;
	private JPanel side_hud_panel;
	private JPanel side_debug_panel;
	private JLabel score_label;
	private JLabel mode_label;
	private JLabel farmed_label;
	private JLabel mined_label;

	public void initFrame() {
		GridBagLayout frame_layout = new GridBagLayout();

		drawing = new DrawingPanel(640, 480, WORLD_WIDTH, WORLD_HEIGHT, this);
		addKeyListener(this);

		getContentPane().add(drawing);

		side_panel = new JPanel();
		side_hud_panel = new JPanel();
		score_label = new JLabel("Score: " + score);
		mode_label = new JLabel("Mode: " + mode);
		farmed_label = new JLabel("Food: " + resources[ResourceGrid.FOOD]);
		mined_label = new JLabel("Minerals: "
				+ resources[ResourceGrid.MINERALS]);

		side_panel.add(score_label);
		side_panel.add(mode_label);
		side_panel.add(farmed_label);
		side_panel.add(mined_label);
		side_panel.add(side_hud_panel);

		side_debug_panel = new JPanel();
		side_debug_panel.setLayout(new MigLayout("fill, flowY"));

		side_panel.add(side_debug_panel);
		side_panel.setLayout(new MigLayout("fill, flowY"));
		side_hud_panel.setLayout(new MigLayout("fill, flowY"));
		side_debug_panel.setLayout(new MigLayout("fill, flowY"));

		final JRadioButton attract_tower_b = new JRadioButton("Attract tower");
		final JRadioButton avoid_tower_b = new JRadioButton("Avoid tower");
		final JRadioButton resource_grid_b = new JRadioButton("Resource grid");
		final JRadioButton avoid_resources_b = new JRadioButton(
				"Avoid resource");

		ButtonGroup button_group = new ButtonGroup();
		button_group.add(attract_tower_b);
		button_group.add(avoid_tower_b);
		button_group.add(resource_grid_b);
		button_group.add(avoid_resources_b);

		ActionListener button_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == attract_tower_b) {
					voxels_to_draw.clear();
					voxels_to_draw.add(attract_tower);
				}
				if (e.getSource() == avoid_tower_b) {
					voxels_to_draw.clear();
					voxels_to_draw.add(avoid_tower);
				}
				if (e.getSource() == resource_grid_b) {
					voxels_to_draw.clear();
					voxels_to_draw.add(resource_grid);
				}
				if (e.getSource() == avoid_resources_b) {
					voxels_to_draw.clear();
					voxels_to_draw.add(resource_grid.avoid_resources);
				}
			}
		};

		attract_tower_b.addActionListener(button_listener);
		avoid_tower_b.addActionListener(button_listener);
		resource_grid_b.addActionListener(button_listener);
		avoid_resources_b.addActionListener(button_listener);

		final JRadioButton tower_b = new JRadioButton("Spawn Tower");
		final JRadioButton zombie_b = new JRadioButton("Spawn Zombie");
		final JRadioButton villager_b = new JRadioButton("Spawn Villager");

		button_group = new ButtonGroup();
		button_group.add(tower_b);
		button_group.add(zombie_b);
		button_group.add(villager_b);
		button_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == tower_b) {
					addMode = ADD_TOWER;
				}
				if (e.getSource() == zombie_b) {
					addMode = ADD_ZOMBIE;
				}
				if (e.getSource() == villager_b) {
					addMode = ADD_VILLAGER;
				}
			}
		};

		tower_b.addActionListener(button_listener);
		zombie_b.addActionListener(button_listener);
		villager_b.addActionListener(button_listener);

		final JCheckBox debug_b = new JCheckBox("Debug mode");
		button_listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (debug_b.isSelected()) {
					mode = DEBUG_MODE;
				} else
					mode = NORMAL_MODE;
			}
		};
		debug_b.addActionListener(button_listener);

		side_debug_panel.add(attract_tower_b);
		side_debug_panel.add(avoid_tower_b);
		side_debug_panel.add(resource_grid_b);
		side_debug_panel.add(avoid_resources_b);
		side_debug_panel.add(debug_b);
		side_debug_panel.add(tower_b);
		side_debug_panel.add(zombie_b);
		side_debug_panel.add(villager_b);

		getContentPane().add(side_panel);

		setLayout(frame_layout);
		pack();
		setVisible(true);
	}

	public LinkedList<Entity> entities;
	public LinkedList<VoxelGrid> voxels_to_draw;
	public ResourceGrid resource_grid;
	public HeightMapGradient attract_tower;
	public HeightMapGradient avoid_tower;
	public HeightMapGradient avoid_edges;
	public Grid grid;

	public GameThread game_thread;

	public void initGame() {
		grid = new Grid(WORLD_WIDTH, WORLD_HEIGHT, 100);

		voxels_to_draw = new LinkedList<VoxelGrid>();

		resource_grid = new ResourceGrid(WORLD_WIDTH, WORLD_HEIGHT, 18);

		attract_tower = new HeightMapGradient(WORLD_WIDTH, WORLD_HEIGHT, 15);
		avoid_tower = new HeightMapGradient(WORLD_WIDTH, WORLD_HEIGHT, 15);
		avoid_edges = new HeightMapGradient(WORLD_WIDTH, WORLD_HEIGHT, 64);

		for (int i = 0; i < avoid_edges.voxels.length; i++) {
			avoid_edges.voxels[i][0] = 1000;
			avoid_edges.voxels[i][avoid_edges.voxels[0].length - 1] = 1000;
		}

		for (int j = 0; j < avoid_edges.voxels[0].length; j++) {
			avoid_edges.voxels[0][j] = 1000;
			avoid_edges.voxels[avoid_edges.voxels.length - 1][j] = 1000;
		}

		entities = new LinkedList<Entity>();

		drawing.view.x = WORLD_WIDTH / 2 - 340;
		drawing.view.y = WORLD_HEIGHT / 2 - 280;

		addTower(WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
	}

	private void addZombie(int x, int y) {
		ZombieEntity entity = new ZombieEntity(x, y, grid);
		entity.addGradient(attract_tower);
		entity.addGradient(avoid_tower);
		entity.addGradient(avoid_edges);
		entity.addGradient(resource_grid.avoid_resources);
		entities.add(entity);
	}

	private void addVillager(int x, int y) {
		VillagerEntity entity = new VillagerEntity(x, y, grid, this,
				resource_grid);
		entity.addGradient(avoid_edges);
		entities.add(entity);
	}

	private void addTower(int x, int y) {
		TowerEntity entity = new TowerEntity(x, y, grid, attract_tower,
				avoid_tower, resource_grid);
		entity.autoFinish();
		entities.add(entity);
	}

	public void startGame() {
		global_events = new LinkedList<GlobalEvent>();
		global_events_buffer = new LinkedList<GlobalEvent>();
		selected = new LinkedList<Entity>();

		game_thread = new GameThread();
		game_thread.start();
	}

	public LinkedList<Entity> selected;

	public void updateHUD() {
		score_label.setText("Score: " + score);
		mode_label.setText("Mode: " + mode);
		farmed_label.setText("Food: " + resources[ResourceGrid.FOOD]);
		mined_label.setText("Minerals: " + resources[ResourceGrid.MINERALS]);
		pack();
	}

	public String createCostString(int[] cost) {
		String result = "";
		for (int resourcei = 0; resourcei < ResourceGrid.RESOURCE_NAMES.length; resourcei++) {
			String resource_name = ResourceGrid.RESOURCE_NAMES[resourcei];
			if (resource_name == null)
				continue;
			result += resource_name + ": " + cost[resourcei] + ", ";
		}
		return result;
	}

	public boolean canAfford(int[] cost) {
		for (int resourcei = 0; resourcei < ResourceGrid.RESOURCE_NAMES.length; resourcei++) {
			if (resources[resourcei] < cost[resourcei])
				return false;
		}
		return true;
	}

	public void decrementResources(int[] cost) {
		for (int resourcei = 0; resourcei < ResourceGrid.RESOURCE_NAMES.length; resourcei++) {
			resources[resourcei] -= cost[resourcei];
		}
	}

	public void setupButtons() {
		side_hud_panel.removeAll();
		if (selected.size() > 0
				&& selected.getFirst() instanceof VillagerEntity) {
			VillagerEntity tower = ((VillagerEntity) selected.getFirst());
			final JCheckBox build = new JCheckBox("Build: "
					+ createCostString(VillagerEntity.MAKE_TOWER_COST));
			side_hud_panel.add(build);
			pack();

			build.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (build.isSelected()) {
						mode = BUILD_MODE;
					} else {
						mode = NORMAL_MODE;
					}
				}
			});

		}
		if (selected.size() > 0 && selected.getFirst() instanceof TowerEntity) {
			TowerEntity tower = ((TowerEntity) selected.getFirst());
			final JButton upgrade = new JButton("Upgrade: "
					+ createCostString(TowerEntity.UPGRADE_COST[tower.level]));
			final JButton villager = new JButton("Make villager: "
					+ createCostString(TowerEntity.MAKE_VILLAGER_COST));
			side_hud_panel.add(upgrade);
			side_hud_panel.add(villager);
			pack();

			upgrade.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TowerEntity tower = ((TowerEntity) selected.getFirst());

					if (canAfford(TowerEntity.UPGRADE_COST[tower.level])) {
						if (tower.upgrade()) {
							decrementResources(TowerEntity.UPGRADE_COST[tower.level - 1]);
							upgrade
									.setText("Upgrade: "
											+ createCostString(TowerEntity.UPGRADE_COST[tower.level]));
						}
					}
				}
			});

			villager.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					TowerEntity tower = ((TowerEntity) selected.getFirst());

					if (canAfford(TowerEntity.MAKE_VILLAGER_COST)) {
						fireGlobalEvent(new AddEntity(this, ADD_VILLAGER,
								(int) tower.point.x, (int) tower.point.y));
						decrementResources(TowerEntity.MAKE_VILLAGER_COST);
					}
				}
			});
		}
	}

	public void select(Rectangle selection, boolean ctrl) {
		if (mode == NORMAL_MODE) {
			if (!ctrl) {
				for (Entity e : selected)
					e.selected = false;
				selected.clear();
			}

			LinkedList<Entity> result = grid.getEntitiesIn(selection);

			boolean only_villagers = false;
			for (Entity e : result) {
				if (e instanceof VillagerEntity) {
					only_villagers = true;
					break;
				}
			}

			if (only_villagers) {
				for (Entity e : result) {
					if (e instanceof VillagerEntity) {
						e.selected = true;
						selected.add(e);
					}
				}
			} else {
				for (Entity e : result) {
					if (e instanceof TowerEntity) {
						e.selected = true;
						selected.add(e);

						setupButtons();
						break;
					}
				}
			}

			setupButtons();
		}
	}

	public void select(Point p, boolean ctrl) {
		if (mode == NORMAL_MODE) {
			if (!ctrl) {
				for (Entity e : selected)
					e.selected = false;
				selected.clear();
			}

			LinkedList<Entity> result = grid.getEntitiesAt(p);
			for (Entity e : result) {
				e.selected = true;
				selected.add(e);
				break;
			}

			setupButtons();
		}

		if (mode == DEBUG_MODE) {
			this.fireGlobalEvent(new AddEntity(this, addMode, p.x, p.y));
		}

		if (mode == BUILD_MODE) {
			if (canAfford(VillagerEntity.MAKE_TOWER_COST)) {
				decrementResources(VillagerEntity.MAKE_TOWER_COST);
				TowerEntity entity = new TowerEntity(p.x, p.y, grid,
						attract_tower, avoid_tower, resource_grid);
				fireGlobalEvent(new DelayedEntity(this, entity));

				for (Entity e : selected) {
					if (e instanceof VillagerEntity) {
						((VillagerEntity) e).startBuilding(entity);
					}
				}
			}

			mode = NORMAL_MODE;
			for (Entity e : selected)
				e.selected = false;
			selected.clear();
		}
	}

	public void rightclick(Point p, boolean ctrl) {
	}

	public class GameThread extends Thread {
		public static final int FPS = 60;

		public void run() {
			long lasttime = System.currentTimeMillis();
			while (true) {
				lasttime = System.currentTimeMillis();

				LinkedList<GlobalEvent> temp_global_events = global_events;
				global_events = global_events_buffer;
				global_events_buffer = temp_global_events;
				for (GlobalEvent event : global_events) {
					if (event instanceof SelectEvent) {
						select(((SelectEvent) event).selection,
								((SelectEvent) event).ctrl);
					}
					if (event instanceof RightClickEvent) {
						rightclick(((RightClickEvent) event).point,
								((RightClickEvent) event).ctrl);
					}
					if (event instanceof SelectPointEvent) {
						select(((SelectPointEvent) event).point,
								((SelectPointEvent) event).ctrl);
					}
					if (event instanceof AddEntity) {
						int x = ((AddEntity) event).x;
						int y = ((AddEntity) event).y;
						switch (((AddEntity) event).type) {
						case ADD_ZOMBIE:
							addZombie(x, y);
							break;
						case ADD_VILLAGER:
							addVillager(x, y);
							break;
						case ADD_TOWER:
							addTower(x, y);
							break;
						}
					}
					if (event instanceof GatherResource) {
						resources[((GatherResource) event).type] += ((GatherResource) event).amount;
					}
					if (event instanceof DelayedEntity) {
						entities.add(((DelayedEntity) event).entity);
					}
				}
				global_events.clear();

				for (Entity entity : entities) {
					entity.think(1.0 / FPS);
				}

				if (Math.random() < 0.01)
					addZombie((int) (Math.random() * WORLD_WIDTH), 0);
				if (Math.random() < 0.01)
					addZombie((int) (Math.random() * WORLD_WIDTH), WORLD_HEIGHT);
				if (Math.random() < 0.01)
					addZombie(0, (int) (Math.random() * WORLD_HEIGHT));
				if (Math.random() < 0.01)
					addZombie(WORLD_WIDTH, (int) (Math.random() * WORLD_HEIGHT));

				Iterator<Entity> entity_it = entities.iterator();
				while (entity_it.hasNext()) {
					Entity e = entity_it.next();
					if (e.health < 0) {
						if (e instanceof ZombieEntity)
							score++;
						if (e instanceof VillagerEntity) {
							fireGlobalEvent(new AddEntity(this, ADD_ZOMBIE,
									(int) e.point.x, (int) e.point.y));
						}
						e.prepareDelete();
						e.grid_location.removeFromList();
						entity_it.remove();
					}
				}

				drawing.render(voxels_to_draw, entities);

				updateHUD();

				try {
					long sleeptime = lasttime + 1000 / FPS
							- System.currentTimeMillis();
					Thread.sleep(sleeptime);
				} catch (IllegalArgumentException e) {
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public LinkedList<GlobalEvent> global_events;
	public LinkedList<GlobalEvent> global_events_buffer;

	public void fireGlobalEvent(GlobalEvent globalEvent) {
		global_events_buffer.add(globalEvent);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}
