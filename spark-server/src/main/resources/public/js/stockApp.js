var app = {
    WS_URL : "ws://localhost:8888/ws/cot",
    //indice : null,
    points1:[],
    points2:[],
    socket : null,
    chart:null,
    nombrePointsGraph :100,
    indices : [],


    initApp : function () {
        this.initWebSocketConnection();
        this.initChart();
        this.initObservable();

    },

    initWebSocketConnection : function () {
        var that = this;

        var openObserver = Rx.Observer.create(function(e) {
            console.info('socket open');
        });

        // an observer for when the socket is about to close
        var closingObserver = Rx.Observer.create(function() {
            console.log('socket is about to close');
        });

        // create a web socket subject
        this.socket = Rx.DOM.fromWebSocket(
            that.WS_URL,
            null,
            openObserver,
            closingObserver);
    },

    initChart : function () {
        this.chart = new CanvasJS.Chart("chartContainer",{
            title :{
                text: "Indice boursiers"
            }
                ,zoomEnabled: true
            ,width:1200
            ,height:600
            ,
            data: [
            ]
        });


    },

    initObservable : function () {
        var that = this;
        this.socket


            .subscribe(
                function( message) {

                    console.log(message)
                    var indice = JSON.parse(message.data);

                    var cours = indice.cours;
                    //indice.points = [];
                    //var that = this;

                    //si indice n'est pas en cache ajout
                    if(!that.alreadyExist(indice.nom)){
                        console.log("new")
                        indice.points = [];

                        that.indices.push(indice);

                        that.chart.options.data.push( {
                                type: "spline",
                                 dataPoints: indice.points,
                                 showInLegend: true,
                                 name: indice.nom
                             })
                    }

                    console.log(indice);

                    //recup de l'objet dans le cache
                    indice = that.getObjectPositionInArray(indice.nom);

                    that.updateChart(indice,cours);
                },
                function(e) {
                    // errors and "unclean" closes land here
                    console.error('error: %s', e);
                },
                function() {
                    // the socket has been closed
                    console.info('socket closed');
                }
            );
    },

    updateChart : function (indice,cours) {

        console.log(cours);

        indice.points.push({
            x: new Date(cours.dateValeur),
            y: cours.valeurCours
        });



        if (indice.points.length > this.nombrePointsGraph){
            indice.points.shift();
        }

        this.chart.render();
    },

    //recherche si l'objet avec le nom passé en paramètre existe déjé dans le cache
    alreadyExist : function ( nom) {

        var nbreElements = this.indices.length;

        if (nbreElements === 0) {
            return false;
        } else {

            for (cpt = 0; cpt < nbreElements; cpt++) {

                var cachingIndice = this.indices[cpt];

                //si l'objet est présent
                if (cachingIndice.nom === nom) {
                    return true;
                }
            }
            //pas de match
            return false;
        }
    },

    //retourne l'objet présent en cache via son identification
    getObjectPositionInArray : function (nom) {
        var nbreElements = this.indices.length;

        for(cpt = 0; cpt < nbreElements; cpt ++){
            var cachingIndice = this.indices[cpt];
            //console.log(cachingIndice);
            if(cachingIndice.nom === nom){
                return this.indices[cpt];
            }
        }

        console.log("Error no object found");
    }

};

window.onload = function () {
    app.initApp();
};