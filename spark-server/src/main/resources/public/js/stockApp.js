var app = {

    wsUrl : '',                 //url du websocket

    wsSubject : null,           //observable basé sur le websocket
    wsObserver : null,          //observer à l'écoute des messages
    chart:null,                 //le graph, composant canvas.js
    nombrePointsGraph :100,     //nombre de ponts max à afficher en même temps
    indices : [],               //tableau des indices à afficher sur le graph

    //ui
    $btnWsConnect : '',         //bouton de connexion au ws
    $wsUrlField : '',           //champ de saisie de l'url


    /**
     * Méthode initialisant l'application avec l'url du websocket en paramètre
     * @param wsServerUrl l'url de la websocket
     */
    initApp : function (wsServerUrl) {
        this.wsUrl = wsServerUrl;
        console.log("app.initApp with ws url: " + this.wsUrl);
        this.initChart();
        this.initGui()
    },

    showGrowl : function (msg,type) {
        $.notify({
            // options
            message: msg
        },{
            // settings
            type: type
        });
    },

    stopSubscribe : function () {
        this.wsObserver.dispose();
    },

    /**
     * Creation sujet: observer connecté au flux serveur etemettant les valeurs
     */
    createWSSubject : function () {
        var that = this;

        //ouverture du flux
        var openObserver = Rx.Observer.create(function(e) {
            console.log('app.sujet : socket open : ' + wsUrl);
            console.log(e);
            that.showGrowl("Socket open : [" + that.wsUrl +"]","success");

        });

        // fermeture du flux
        var closingObserver = Rx.Observer.create(function() {
            console.log('app.sujet : socket is about to close');
            that.showGrowl("Socket closed : [" + that.wsUrl +"]","danger");
            that.switchConnectState("false");

            that.$wsUrlField.removeAttr("readonly");
        });

        // création du sujet
        this.wsSubject = Rx.DOM.fromWebSocket(
            that.wsUrl,
            null,
            openObserver,
            closingObserver);
    },

    createWSObserver : function () {
        var that = this;

        this.wsObserver = this.wsSubject.subscribe(
            function( message) {

                console.log(message);
                var indice = JSON.parse(message.data);

                var cours = indice.cours;

                //si indice n'est pas en cache ajout
                if(!that.alreadyExist(indice.nom)){
                    indice.points = [];

                    that.indices.push(indice);

                    that.chart.options.data.push( {
                        type: "spline",
                        dataPoints: indice.points,
                        showInLegend: true,
                        name: indice.nom
                    })
                }

                //recup de l'objet dans le cache
                indice = that.getObjectPositionInArray(indice.nom);

                that.updateChart(indice,cours);
            },
            function(e) {
                // errors and "unclean" closes land here
                console.error('app.observer : error: ' + e);
                that.wsObserver.dispose();
                that.showGrowl("Error happening during ws subject subscription : [" + that.wsUrl +"]","danger");

            },
            function() {
                // the socket has been closed
                console.info('app.observer : socket closed');
            }
        );
    },

    /**
     * Création de l'objet chart (composant canvas.js
     */
    initChart : function () {
        this.chart = new CanvasJS.Chart("chartContainer",{
            title :{
                text: "Indice boursiers"
            },
            zoomEnabled: true,
            data : []
        });
    },

    /**
     * Initialisatons des objets de bases html
     */
    initGui : function () {
        var that = this;

        this.$btnWsConnect = $('#btnWsConnect');
        this.$wsUrlField = $('#wsUrl');

        //evenement click via observable
        var connectBtnClickObservable = Rx.DOM.click(this.$btnWsConnect[0]);

        var subscription = connectBtnClickObservable.subscribe(

            function (mouseEvent) {
                var isConnectState = (that.$btnWsConnect.attr("data-b-isconnect") === 'true');
                that.switchConnectState(isConnectState);

            });
    },

    /**
     * Change les composant ui en fonction de l'état de connexion au webservice
     *
     * @param isConnectState
     */
    switchConnectState : function (isConnectState) {

        var that = this;
        var isconnect,labelConnect;

        if(isConnectState){
            console.log(isConnectState);

            that.stopSubscribe();
            //ui
            isconnect = "false";
            labelConnect = "Connect";
            that.$btnWsConnect.removeClass("btn-danger").addClass("btn-success");
            that.$wsUrlField.removeAttr("readonly","readonly");

        }else{
            console.log(isConnectState);


            that.createWSSubject();
            that.createWSObserver();
            that.refreshChart();

            isconnect = "true";
            labelConnect = "Disconnect";
            that.wsUrl = that.$wsUrlField.val();
            that.$btnWsConnect.removeClass("btn-success").addClass("btn-danger");
            that.$wsUrlField.attr("readonly","readonly");

        }

        that.$btnWsConnect.attr("data-b-isconnect",isconnect);
        that.$btnWsConnect.html(labelConnect);

    },


    refreshChart : function () {
        this.chart.options.data = [];
        this.indices = [];
        this.chart.render();
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
        console.log(1);

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

