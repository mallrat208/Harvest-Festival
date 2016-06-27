package joshie.harvest.api.calendar;

import joshie.harvest.api.core.ISeasonData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public interface ICalendar {
    ICalendarDate getDate(World world);
    ICalendarDate cloneDate(ICalendarDate date);
    ICalendarDate newDate(int day, Season season, int year);
    ISeasonData getDataForSeason(Season season);

    /** Returns the date at this location
     *  @param world the world to check
     *  @param pos the block position to check
     *  @return the season at these coordinates**/
    Season getSeasonAtCoordinates(World world, BlockPos pos);
}