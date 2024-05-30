package me.infinity.groupstats.core;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.infinity.groupstats.api.GroupNode;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class GroupStatsExpansion extends PlaceholderExpansion {

  private final GroupStatsPlugin instance;

  @Override
  public @NotNull String getIdentifier() {
    return "groupstats";
  }

  @Override
  public @NotNull String getAuthor() {
    return "infinity";
  }

  @Override
  public @NotNull String getVersion() {
    return instance.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
    if (player == null) {
      return null;
    }

    String result;
    String[] args = params.split("_");
    if (args[0] == null || args[1] == null) {
      return "INVALID_PLACEHOLDER";
    }
    String groupName = args[0];
    String statisticType = args[1];

    GroupProfile profile = instance.getGroupManager().fetchUnsafe(player.getUniqueId());
    if (profile == null) {
      return "0";
    }

    ConcurrentHashMap<String, GroupNode> stats = GroupStatsPlugin.GSON.fromJson(profile.getData(),
        GroupStatsPlugin.STATISTIC_MAP_TYPE);

    if (groupName.equals("overAll")) {
      GroupNode solo = stats.getOrDefault("Solo", new GroupNode());
      GroupNode doubles = stats.getOrDefault("Doubles", new GroupNode());
      GroupNode triples = stats.getOrDefault("3v3v3v3", new GroupNode());
      GroupNode quadruple = stats.getOrDefault("4v4v4v4", new GroupNode());
      GroupNode quadruple1 = stats.getOrDefault("4v4", new GroupNode());
      if (solo == null) {
        return "0";
      }
      if (doubles == null) {
        return "0";
      }
      if (triples == null) {
        return "0";
      }
      if (quadruple == null) {
        return "0";
      }
      if (quadruple1 == null) {
        return "0";
      }

      int overAllGamesPlayed = solo.getGamesPlayed() + doubles.getGamesPlayed() + triples.getGamesPlayed() + quadruple.getGamesPlayed() + quadruple1.getGamesPlayed();
      int overAllBedsBroken = solo.getBedsBroken() + doubles.getBedsBroken() + triples.getBedsBroken() + quadruple.getBedsBroken() + quadruple1.getBedsBroken();
      int overAllBedsLost = solo.getBedsLost() + doubles.getBedsLost() + triples.getBedsLost() + quadruple.getBedsLost() + quadruple1.getBedsLost();
      int overAllKills = solo.getKills() + doubles.getKills() + triples.getKills() + quadruple.getKills() + quadruple1.getKills();
      int overAllDeaths = solo.getDeaths() + doubles.getDeaths() + triples.getDeaths() + quadruple.getDeaths() + quadruple1.getDeaths();
      int overAllFinalKills = solo.getFinalKills() + doubles.getFinalKills() + triples.getFinalKills() + quadruple.getFinalKills() + quadruple1.getFinalKills();
      int overAllFinalDeaths = solo.getFinalDeaths() + doubles.getFinalDeaths() + triples.getFinalDeaths() + quadruple.getFinalDeaths() + quadruple1.getFinalDeaths();
      int overAllWins = solo.getWins() + doubles.getWins() + triples.getWins() + quadruple.getWins() + quadruple1.getWins();
      int overAllLosses = solo.getLosses() + doubles.getLosses() + triples.getLosses() + quadruple.getLosses() + quadruple1.getLosses();

      switch (statisticType) {
        case "gamesPlayed":
          result = String.valueOf(overAllGamesPlayed);
          break;
        case "bedsBroken":
          result = String.valueOf(overAllBedsBroken);
          break;
        case "bedsLost":
          result = String.valueOf(overAllBedsLost);
          break;
        case "kills":
          result = String.valueOf(overAllKills);
          break;
        case "deaths":
          result = String.valueOf(overAllDeaths);
          break;
        case "finalKills":
          result = String.valueOf(overAllFinalKills);
          break;
        case "finalDeaths":
          result = String.valueOf(overAllFinalDeaths);
          break;
        case "wins":
          result = String.valueOf(overAllWins);
          break;
        case "losses":
          result = String.valueOf(overAllLosses);
          break;
          // Not the correct way of getting win streak
        case "winstreak":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getWinstreak).sum());
          break;
        case "highestWinstreak":
          result = String.valueOf(stats.isEmpty() ? 0
              : stats.values().stream().mapToInt(GroupNode::getHighestWinstreak).max().getAsInt());
          break;
          // End of win streak
        case "kdr":
          if (overAllKills == 0 && overAllDeaths == 0) {
            result = String.valueOf(0.00);
          } else if (overAllKills == 0) {
            result = String.valueOf(0.00);
          } else if (overAllDeaths == 0) {
            result = String.valueOf(overAllKills);
          } else {
            double kdr = ((double) overAllKills) / ((double) overAllDeaths);
            DecimalFormat kdrDf = new DecimalFormat("#.##");
            result = String.valueOf(kdrDf.format(kdr));
          }
          break;
        case "fkdr":
          if (overAllFinalKills == 0 && overAllFinalDeaths == 0) {
            result = String.valueOf(0.00);
          } else if (overAllFinalKills == 0) {
            result = String.valueOf(0.00);
          } else if (overAllFinalDeaths == 0) {
            result = String.valueOf(overAllFinalKills);
          } else {
            double fkdr = ((double) overAllFinalKills) / ((double) overAllFinalDeaths);
            DecimalFormat fkdrDf = new DecimalFormat("#.##");
            result = String.valueOf(fkdrDf.format(fkdr));
          }
          break;
        case "bblr":
          if (overAllBedsBroken == 0 && overAllBedsLost == 0) {
            result = String.valueOf(0.00);
          } else if (overAllBedsBroken == 0) {
            result = String.valueOf(0.00);
          } else if (overAllBedsLost == 0) {
            result = String.valueOf(overAllBedsBroken);
          } else {
            double bblr = ((double) overAllBedsBroken) / ((double) overAllBedsLost);
            DecimalFormat bblrDf = new DecimalFormat("#.##");
            result = String.valueOf(bblrDf.format(bblr));
          }
          break;
        case "wlr":
          if (overAllWins == 0 && overAllLosses == 0) {
            result = String.valueOf(0.00);
          } else if (overAllWins == 0) {
            result = String.valueOf(0.00);
          } else if (overAllLosses == 0) {
            result = String.valueOf(overAllWins);
          } else {
            double wlr = ((double) overAllWins) / ((double) overAllLosses);
            DecimalFormat wlrDf = new DecimalFormat("#.##");
            result = String.valueOf(wlrDf.format(wlr));
          }
          break;
        default:
          result = "0";
          break;
      }
      return result;
    }

    if (groupName.equals("core")) {
      GroupNode solo = stats.getOrDefault("Solo", new GroupNode());
      GroupNode doubles = stats.getOrDefault("Doubles", new GroupNode());
      GroupNode triples = stats.getOrDefault("3v3v3v3", new GroupNode());
      GroupNode quadruple = stats.getOrDefault("4v4v4v4", new GroupNode());
      if (solo == null) {
        return "0";
      }
      if (doubles == null) {
        return "0";
      }
      if (triples == null) {
        return "0";
      }
      if (quadruple == null) {
        return "0";
      }

      int coreGamesPlayed = solo.getGamesPlayed() + doubles.getGamesPlayed() + triples.getGamesPlayed() + quadruple.getGamesPlayed();
      int coreBedsBroken = solo.getBedsBroken() + doubles.getBedsBroken() + triples.getBedsBroken() + quadruple.getBedsBroken();
      int coreBedsLost = solo.getBedsLost() + doubles.getBedsLost() + triples.getBedsLost() + quadruple.getBedsLost();
      int coreKills = solo.getKills() + doubles.getKills() + triples.getKills() + quadruple.getKills();
      int coreDeaths = solo.getDeaths() + doubles.getDeaths() + triples.getDeaths() + quadruple.getDeaths();
      int coreFinalKills = solo.getFinalKills() + doubles.getFinalKills() + triples.getFinalKills() + quadruple.getFinalKills();
      int coreFinalDeaths = solo.getFinalDeaths() + doubles.getFinalDeaths() + triples.getFinalDeaths() + quadruple.getFinalDeaths();
      int coreWins = solo.getWins() + doubles.getWins() + triples.getWins() + quadruple.getWins();
      int coreLosses = solo.getLosses() + doubles.getLosses() + triples.getLosses() + quadruple.getLosses();

      switch (statisticType) {
        case "gamesPlayed":
          result = String.valueOf(coreGamesPlayed);
          break;
        case "bedsBroken":
          result = String.valueOf(coreBedsBroken);
          break;
        case "bedsLost":
          result = String.valueOf(coreBedsLost);
          break;
        case "kills":
          result = String.valueOf(coreKills);
          break;
        case "deaths":
          result = String.valueOf(coreDeaths);
          break;
        case "finalKills":
          result = String.valueOf(coreFinalKills);
          break;
        case "finalDeaths":
          result = String.valueOf(coreFinalDeaths);
          break;
        case "wins":
          result = String.valueOf(coreWins);
          break;
        case "losses":
          result = String.valueOf(coreLosses);
          break;
        // Not the correct way of getting win streak
        case "winstreak":
          result = String.valueOf(stats.isEmpty() ? 0
                  : stats.values().stream().mapToInt(GroupNode::getWinstreak).sum());
          break;
        case "highestWinstreak":
          result = String.valueOf(stats.isEmpty() ? 0
                  : stats.values().stream().mapToInt(GroupNode::getHighestWinstreak).max().getAsInt());
          break;
        // End of win streak
        case "kdr":
          if (coreKills == 0 && coreDeaths == 0) {
            result = String.valueOf(0.00);
          } else if (coreKills == 0) {
            result = String.valueOf(0.00);
          } else if (coreDeaths == 0) {
            result = String.valueOf(coreKills);
          } else {
            double kdr = ((double) coreKills) / ((double) coreDeaths);
            DecimalFormat kdrDf = new DecimalFormat("#.##");
            result = String.valueOf(kdrDf.format(kdr));
          }
          break;
        case "fkdr":
          if (coreFinalKills == 0 && coreFinalDeaths == 0) {
            result = String.valueOf(0.00);
          } else if (coreFinalKills == 0) {
            result = String.valueOf(0.00);
          } else if (coreFinalDeaths == 0) {
            result = String.valueOf(coreFinalKills);
          } else {
            double fkdr = ((double) coreFinalKills) / ((double) coreFinalDeaths);
            DecimalFormat fkdrDf = new DecimalFormat("#.##");
            result = String.valueOf(fkdrDf.format(fkdr));
          }
          break;
        case "bblr":
          if (coreBedsBroken == 0 && coreBedsLost == 0) {
            result = String.valueOf(0.00);
          } else if (coreBedsBroken == 0) {
            result = String.valueOf(0.00);
          } else if (coreBedsLost == 0) {
            result = String.valueOf(coreBedsBroken);
          } else {
            double bblr = ((double) coreBedsBroken) / ((double) coreBedsLost);
            DecimalFormat bblrDf = new DecimalFormat("#.##");
            result = String.valueOf(bblrDf.format(bblr));
          }
          break;
        case "wlr":
          if (coreWins == 0 && coreLosses == 0) {
            result = String.valueOf(0.00);
          } else if (coreWins == 0) {
            result = String.valueOf(0.00);
          } else if (coreLosses == 0) {
            result = String.valueOf(coreWins);
          } else {
            double wlr = ((double) coreWins) / ((double) coreLosses);
            DecimalFormat wlrDf = new DecimalFormat("#.##");
            result = String.valueOf(wlrDf.format(wlr));
          }
          break;
        default:
          result = "0";
          break;
      }
      return result;
    }

    GroupNode groupNode = stats.get(groupName);
    if (groupNode == null) {
      return "0";
    }

    switch (statisticType) {
      case "gamesPlayed":
        result = String.valueOf(groupNode.getGamesPlayed());
        break;
      case "bedsBroken":
        result = String.valueOf(groupNode.getBedsBroken());
        break;
      case "bedsLost":
        result = String.valueOf(groupNode.getBedsLost());
        break;
      case "kills":
        result = String.valueOf(groupNode.getKills());
        break;
      case "deaths":
        result = String.valueOf(groupNode.getDeaths());
        break;
      case "finalKills":
        result = String.valueOf(groupNode.getFinalKills());
        break;
      case "finalDeaths":
        result = String.valueOf(groupNode.getFinalDeaths());
        break;
      case "wins":
        result = String.valueOf(groupNode.getWins());
        break;
      case "losses":
        result = String.valueOf(groupNode.getLosses());
        break;
      case "winstreak":
        result = String.valueOf(groupNode.getWinstreak());
        break;
      case "highestWinstreak":
        result = String.valueOf(groupNode.getHighestWinstreak());
        break;
      case "kdr":
        result = String.valueOf(this.getRatio(groupNode, "kdr"));
        break;
      case "fkdr":
        result = String.valueOf(this.getRatio(groupNode, "fkdr"));
        break;
      case "bblr":
        result = String.valueOf(this.getRatio(groupNode, "bblr"));
        break;
      case "wlr":
        result = String.valueOf(this.getRatio(groupNode, "wlr"));
        break;
      default:
        result = "0";
        break;
    }
    return result;
  }

  public double getRatio(GroupNode groupNode, String type) {
    double result;
    switch (type) {
      case "kdr":
        int deaths = groupNode.getDeaths();
        if (deaths == 0) {
          deaths = 1;
        }
        result = this.getRatio(groupNode.getKills(), deaths);
        break;
      case "fkdr":
        int finalDeaths = groupNode.getFinalDeaths();
        if (finalDeaths == 0) {
          finalDeaths = 1;
        }
        result = this.getRatio(groupNode.getFinalKills(), finalDeaths);
        break;
      case "bblr":
        int bedsLost = groupNode.getBedsLost();
        if (bedsLost == 0) {
          bedsLost = 1;
        }
        result = this.getRatio(groupNode.getBedsBroken(), bedsLost);
        break;
      case "wlr":
        int losses = groupNode.getLosses();
        if (losses == 0) {
          losses = 1;
        }
        result = this.getRatio(groupNode.getWins(), losses);
        break;
      default:
        result = Double.NaN;
        break;
    }
    return result;
  }

  public double getRatio(int i1, int i2) {
    if (i2 == 0) {
      // Handle division by zero error here, e.g., return Double.NaN or throw an exception.
      return Double.NaN;
    }

    double value = (double) i1 / i2;
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }
}
