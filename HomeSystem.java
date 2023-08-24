import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.ChatColor;
//SrVoids Copyright, Abaixo o código pedido.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HomePlugin extends JavaPlugin implements CommandExecutor {

    private Map<Player, Inventory> homeInventories = new HashMap<>();
    private Connection connection;

    @Override
    public void onEnable() {
        // Conectar ao banco de dados SQLite
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugin.db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getCommand("homes").setExecutor(this);
        getCommand("addhome").setExecutor(this);
        getCommand("removehome").setExecutor(this);
    }

  

    private void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS homes (" +
                "player_uuid TEXT NOT NULL," +
                "home_name TEXT NOT NULL," +
                "PRIMARY KEY (player_uuid, home_name));";
        connection.createStatement().executeUpdate(query);
    }



    private void openHomeInventory(Player player) {
        Inventory homeInventory = homeInventories.get(player);
        if (homeInventory == null) {
            homeInventory = Bukkit.createInventory(player, 9, "Your Homes");

            // Adicione itens ao inventário
            readHomes(player, homeInventory);

            homeInventories.put(player, homeInventory);
        }
        player.openInventory(homeInventory);
    }

    private void readHomes(Player player, Inventory homeInventory) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT home_name FROM homes WHERE player_uuid = ?;");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String homeName = resultSet.getString("home_name");
                homeInventory.addItem(createHomeItem(homeName));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack createHomeItem(String homeName) {
        ItemStack homeItem = new ItemStack(Material.BED);
        homeItem.setAmount(1);
        homeItem.getItemMeta().setDisplayName(ChatColor.YELLOW + homeName + " Home");
        return homeItem;
    }

  
              }
