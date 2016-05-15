    var map;
    var trackingObjects = [];
    var $consoleGui;
    const MAP_CENTER = {lat: 47.9545, lng: 5.379};
    const MAP_ZOOM = 6;

    // Creation du sujet WebSocket
    var socket = Rx.DOM.fromWebSocket(
        'ws://localhost:8888/ws/gps',
        null, // no protocol
        openObserver,
        closingObserver);

    //observer pour connexion ouverte
    var openObserver = Rx.Observer.create(function(e) {
        console.info('socket open');
    });

    // an observer for when the socket is about to close
    var closingObserver = Rx.Observer.create(function() {
        console.log('socket is about to close');
    });

    //Retourne une instance de polyline pour chaque objet
    function getObjectTrackMap () {
        var polyline =  new google.maps.Polyline({
            strokeColor: generateCouleurAleatoire(),
            strokeOpacity: 1.0,
            strokeWeight: 3
        });

        polyline.setMap(map);

        return polyline;
    }

    //initialisation de la carte
    function initMap() {

        map = new google.maps.Map(document.getElementById('map'), {
            center: MAP_CENTER,
            zoom: MAP_ZOOM
        });

        //init objet div console html
        $consoleGui = $('#messages');
    }

    //retourne l'objet présent en cache via son identification
    function getObjectPositionInArray (identification) {
        var nbreElements = trackingObjects.length;

        for(cpt = 0; cpt < nbreElements; cpt ++){
            var trackingObjectPresent = trackingObjects[cpt];

            if(trackingObjectPresent.identification === identification){
                return trackingObjects[cpt];
            }
        }

        console.log("Error no object found");
    }

    //recherche si l'objet avec l'identification passé en paramètre existe déjé dans le cache
    function alreadyExist ( identification) {

        var nbreElements = trackingObjects.length;

        if(nbreElements === 0){
            return false;
        }else{

            for(cpt = 0; cpt < nbreElements; cpt ++){

                var trackingObjectPresent = trackingObjects[cpt];

                //si l'objet est présent
                if(trackingObjectPresent.identification === identification) {
                    return true;
                }
            }
            //pas de match
            return false;
        }

    }

    //generation d'un code couleur aleatoire
    function generateCouleurAleatoire () {
        var colorCode = "";

        for(cpt = 0; cpt < 6; cpt++){
            var charCode = 0;

            while((charCode < 48 || charCode > 57) && (charCode < 65 || charCode > 69)){
                charCode = Math.floor((Math.random() * 100) + 48);
            }

            colorCode += String.fromCharCode(charCode);
        }

        return "#" + colorCode;
    }

    // ***************** observer pour affichage des points
    socket
    .subscribe(function(wsMessage) {

            //conversion objet
            var objectPosition = JSON.parse(wsMessage.data);

            var lat = objectPosition.lat;
            var lng = objectPosition.lng;

            //si l'objet n'existe pas on l'ajoute dans le cache
            if(!alreadyExist(objectPosition.trackingObject.identification)){
                trackingObjects.push(objectPosition.trackingObject);
            }

            //recup de l'objet dans le cache
            objectPosition = getObjectPositionInArray(objectPosition.trackingObject.identification);

            //si mapTrack non défini, on ajoute --> polyline
            if(objectPosition.mapTrack === undefined){
                objectPosition.mapTrack = getObjectTrackMap();
            }
            //ajout segment
            var path = objectPosition.mapTrack.getPath();
            path.push(new google.maps.LatLng(lat,lng));

        },
        function(e) { console.error('error: %s', e); },
        function() { console.info('socket closed'); }
    );

    //***************** observer pour affichage console
    socket
        .bufferWithCount(3)
        .subscribe(function (fiveGroupedWsMessage){

            $consoleGui.html("");

            fiveGroupedWsMessage.forEach(function (msg) {
                $consoleGui.append('<p>' + msg.data + '</p>');
            });
        },
        function (e) { console.error('error: %s', e); },
        function () { console.info('socket closed');}
    );






