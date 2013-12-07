<?php
/* Hey, it does stuff! */
date_default_timezone_set('America/New_York');
require_once('simple_html_dom.php');

define(CD_DFO_DINNER, "http://www.campusdish.com/en-US/CSNE/Rochester/Menus/DanforthFreshFoodCompany.htm?LocationName=Danforth%20Fresh%20Food%20Company&MealID=17&OrgID=195030&Date=DATEHOLDER&ShowPrice=False&ShowNutrition=True");
define(CD_DFO_LUNCH, "http://www.campusdish.com/en-US/CSNE/Rochester/Menus/DanforthFreshFoodCompany.htm?LocationName=Danforth%20Fresh%20Food%20Company&MealID=16&OrgID=195030&Date=DATEHOLDER&ShowPrice=False&ShowNutrition=True");
define(CD_DOUG_DINNER, "http://www.campusdish.com/en-US/CSNE/Rochester/Menus/DouglassDiningCenter.htm?LocationName=Douglass%20Dining%20Center&MealID=17&OrgID=195030&Date=DATEHOLDER&ShowPrice=False&ShowNutrition=True");
define(CD_DOUG_LUNCH, "http://www.campusdish.com/en-US/CSNE/Rochester/Menus/DouglassDiningCenter.htm?LocationName=Douglass%20Dining%20Center&MealID=16&OrgID=195030&Date=DATEHOLDER&ShowPrice=False&ShowNutrition=True");
define(CD_DOUG_BREAK, "http://www.campusdish.com/en-US/CSNE/Rochester/Menus/DouglassDiningCenter.htm?LocationName=Douglass%20Dining%20Center&MealID=1&OrgID=195030&Date=DATEHOLDER&ShowPrice=False&ShowNutrition=True");




function parse_campus_dish($name, $url) {
    $startdate = strtotime('last Sunday');

    $url = str_replace("DATEHOLDER", date("n_j_Y", $startdate), $url);
    $html = file_get_html($url);

    foreach ($html->find('td.ConceptTabText') as $zone) {
        $stations[] = mb_convert_case(trim(htmlspecialchars_decode($zone->innertext)), MB_CASE_TITLE);
    }


    $mealcount = 0;
    foreach ($html->find('td.menuBorder') as $day) {
        unset($meal);
        foreach ($day->find('a') as $mealitem)
            $meal[] = mb_convert_case(trim(htmlspecialchars_decode($mealitem->innertext)), MB_CASE_TITLE);

        $meals[$mealcount / 7][$mealcount % 7] = $meal ? $meal : null;
        $mealcount += 1;
    }

    // Make the menus!
    for ($i = 0; $i < 7; $i++) {
        $m = new DiningHallMenu();
        $m->location = $name;
        for ($j = 0; $j < count($stations); $j++) {
            $m->stations[$j]['name'] = $stations[$j];
            $m->stations[$j]['food'] = $meals[$j][$i];
        }

        $menus[] = $m;
    }
    
    return $menus;
}

function parse_campus_dish_for_day($name, $url, $daywanted) {
    $startdate = strtotime('last Sunday');

    $url = str_replace("DATEHOLDER", date("n_j_Y", $startdate), $url);
    $html = file_get_html($url);

    foreach ($html->find('td.ConceptTabText') as $zone) {
        $stations[] = mb_convert_case(trim(htmlspecialchars_decode($zone->innertext)), MB_CASE_TITLE);
    }


    $mealcount = 0;
    foreach ($html->find('td.menuBorder') as $day) {
        unset($meal);
        foreach ($day->find('a') as $mealitem)
            $meal[] = mb_convert_case(trim(htmlspecialchars_decode($mealitem->innertext)), MB_CASE_TITLE);

        $meals[$mealcount / 7][$mealcount % 7] = $meal ? $meal : null;
        $mealcount += 1;
    }

    // Make the menus!
    for ($i = 0; $i < 7; $i++) {
        $m = new DiningHallMenu();
        $m->location = $name;
        for ($j = 0; $j < count($stations); $j++) {
            $m->stations[$j]['name'] = $stations[$j];
            $m->stations[$j]['food'] = $meals[$j][$i];
        }

        $menus[] = $m;
    }
    
    return $menus[$daywanted];
}

function add_event($title, $open, $close, $location, $description, $hostedby, $cost, $email) {

    $happyid = uniqid();

    $db = new SQLite3('/home/ryanpuff/public_html/dining/dining.sqlite');
    $stmt = $db->prepare("INSERT INTO events (title, availableFrom, availableTo, location, description, hostedBy, cost, email, valkey) VALUES (:title, :open, :close, :location, :description, :host, :cost, :email, :valkey);");
    $stmt->bindParam(':title', $title);
    $stmt->bindParam(':open', $open);
    $stmt->bindParam(':close', $close);
    $stmt->bindParam(':location', $location);
    $stmt->bindParam(':description', $description);
    $stmt->bindParam(':host', $hostedBy);
    $stmt->bindParam(':cost', $cost);
    $stmt->bindParam(':email', $email);
    $stmt->bindParam(':valkey', $happyid);

    if (!$stmt->execute())
        die ("Bro I broke stuff.");

    $db = null;

    $message = "Thank you for registering the event titled \"" . $title . "\" on UR Dining App. Please click the link below to confirm your submission and help us prevent spam.\r\nhttp://www.ryanpuffer.com/dining/confirm.php?id=" . $happyid;

    mail($email, "Confirm your event submission", $message, 'From: diningapp@ryanpuffer.com');
}

function get_events($startdate) {
    $db = new SQLite3('/home/ryanpuff/public_html/dining/dining.sqlite');
    $stmt = $db->prepare("SELECT title, availableFrom, availableTo, location, description, hostedBy, cost FROM events WHERE availableFrom >= :from ORDER BY availableFrom");
    $stmt->bindParam(':from', $startdate);
    $result = $stmt->execute();
    while ($r = $result->fetchArray(SQLITE3_ASSOC)) {
        $e = new EventItem();
        $e->title = $r['title'];
        $e->open = date('Y-m-d\TH:i', $r['availableFrom']);
        $e->close = date('Y-m-d\TH:i', $r['availableTo']);
        $e->location = $r['location'];
        $e->description = $r['description'];
        $e->hostedby = $r['hostedBy'];
        $e->cost = $r['cost'];
        $results[] = $e;
    }

    return $results;
}



class DiningHallMenu {
    public $location;
    public $open;
    public $close;
    public $stations = array();
}
class EventItem {
    public $title;
    public $open;
    public $close;
    public $location;
    public $description;
    public $hostedby;
    public $cost;
}

giveTJInfo();

function giveTJInfo() {
    $hour = date('G');
    $day = date('w');


    if ($hour < 11)
        $dining[] = parse_campus_dish_for_day("Douglass", CD_DOUG_BREAK, $day);
    else if ($hour < 17) {
        $dining[] = parse_campus_dish_for_day("Douglass", CD_DOUG_LUNCH, $day);
        $dining[] = parse_campus_dish_for_day("Danforth", CD_DFO_LUNCH, $day);
    }
    else {
        $dining[] = parse_campus_dish_for_day("Danforth", CD_DFO_DINNER, $day);
        $dining[] = parse_campus_dish_for_day("Douglass", CD_DOUG_DINNER, $day);
    }

    $events = get_events($day);

    $final = array(dininghalls => $dining, events => $events);
    var_dump($final);

    return json_encode(array(dininghalls => $dining, events => $events));
}

?>