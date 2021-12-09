#    remove_conditional_maxspeed.py - Remove ceratin maxspeed tagging from OSM data in Poland
#    Copyright (C) 2021 TomTom International B.V.
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.
import logging
import re
from osm_bot_abstraction_layer.generic_bot_retagging import run_simple_retagging_task


tags_to_remove = [
    "maxspeed:conditional",
    "maxspeed:backward:conditional",
    "maxspeed:bus:backward:conditional",
    "maxspeed:bus:conditional",
    "maxspeed:bus:forward:conditional",
    "maxspeed:forward:conditional",
    "maxspeed:hgv:backward:conditional",
    "maxspeed:hgv:conditional",
    "maxspeed:hgv:forward:conditional",
    "maxspeed:trailer:backward:conditional",
    "maxspeed:trailer:conditional",
    "maxspeed:trailer:forward:conditional",
]

tags_to_update = [
    "maxspeed:bus",
    "maxspeed:bus:forward",
    "maxspeed:bus:backward",
    "maxspeed:trailer",
    "maxspeed:trailer:forward",
    "maxspeed:trailer:backward",
]


def edit_element(tags):
    """Make changes to element tagging."""
    if tags.get('maxspeed') != ("50"):
        return tags
    nigth_value_regex = re.compile(r"^60\s*@\s*\(23:00-0?5:00\)$")
    day_value_regex = re.compile(r"^50\s*@\s*\(0?5:00-23:00\)$")
    day_and_night_value_regex = re.compile(r"^50\s*@\s*\(0?5:00-23:00\)[;\s]+60\s*@\s*\(23:00-0?5:00\)$")
    night_and_day_value_regex = re.compile(r"^60\s*@\s*\(23:00-0?5:00\)[;\s]+50\s*@\s*\(0?5:00-23:00\)$")
    for key in tags_to_remove:
        if tags.get(key) is None:
            continue
        logging.debug("Key %s, value: '%s'", key, tags.get(key))
        logging.debug(nigth_value_regex.match(tags.get(key)))
        if (nigth_value_regex.match(tags.get(key))
                or day_value_regex.match(tags.get(key))
                or day_and_night_value_regex.match(tags.get(key))
                or night_and_day_value_regex.match(tags.get(key))):
            tags.pop(key, None)

    return tags


def main():
    """Run processing."""
    logging.basicConfig(
        format="[%(asctime)s] %(levelname)s: %(message)s",
        level=logging.getLevelName('INFO'),
        datefmt="%Y-%m-%d %H:%M:%S %Z")

    run_simple_retagging_task(
        max_count_of_elements_in_one_changeset=500,
        objects_to_consider_query="""
[out:xml][timeout:25000];
area["name"="powiat pabianicki"]->.searchArea;
(
  wr(area.searchArea)[~"^maxspeed.*conditional$"~"60.*@.*(23:00-0?5:00)"];
  wr(area.searchArea)[~"^maxspeed.*conditional$"~"50.*@.*(0?5:00-23:00)"];
) -> .contitionals;
wr.contitionals[maxspeed];
out body;
>;
out skel qt;
""",
        objects_to_consider_query_storage_file='conditional_maxspeed.osm',
        is_in_manual_mode=True,
        changeset_comment='Usuwanie ograniczenia do 60 km/h w godzinach nocnych w obszarze zabudowanym.',
        discussion_url='https://forum.openstreetmap.org/viewtopic.php?id=73116',
        osm_wiki_documentation_page='https://wiki.openstreetmap.org/wiki/Automated_edits/TTmechanicalupdates/Remove_night_time_conditional_speed_restriction_in_urban_areas_in_Poland',
        edit_element_function=edit_element,
    )


main()
