var app = {

    wsUrl : '',                 //url du websocket
    restUrl : '',
    wsSubject : null,           //observable basé sur le websocket
    wsObserver : null,          //observer à l'écoute des messages
    chart:null,                 //le graph, composant canvas.js
    nombrePointsGraph :20,     //nombre de ponts max à afficher en même temps
    indicesCache : [],          //indices mis en cache

    //ui
    $btnWsConnect : '',         //bouton de connexion au ws
    $wsUrlField : '',           //champ de saisie de l'url


    /**
     * Méthode initialisant l'application avec l'url du websocket en paramètre
     * @param wsServerUrl l'url de la websocket
     */
    initApp : function (wsServerUrl,restUrl) {
        this.wsUrl = wsServerUrl;
        this.restUrl = restUrl;
        //this.initDatas();
        console.log("app.initApp with ws url: " + this.wsUrl);
        this.initChart();
        this.initGui()
    },

    initDatas : function () {
        var that = this;
        //Appel rest vers
        Rx.DOM.ajax({ method : "GET", url: that.restUrl, responseType: 'json'})
            .subscribe(
                function (data) {
                    data.response.forEach(function (product) {
                        console.log(product);
                    });
                },
                function (error) {
                    // Log the error
                }
            );
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
     * Creation sujet: observer connecté au flux serveur et emettant les valeurs
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

    indiceSet : function (nomIndice) {

        var isSet = false;

        $('#legendContainer input:checked').each(function() {

            console.log("Element: " + $(this).attr('name'));
            console.log("Element to match: " + nomIndice);

            if($(this).attr('name') === nomIndice){
                isSet = true;
            }
        });

        return isSet;


    },

    createWSObserver : function () {
        var that = this;

        this.wsObserver = this.wsSubject
            .filter(function (message){
                //recuperation du message et conversion en objet
                var indice = JSON.parse(message.data);

                return indice.typeValeur === 'INDICE_BOURSIER'

                && that.indiceSet(indice.nom); // on limite auax indices choisis
            })
            .subscribe(
                function( message) {

                    console.log(message);
                    //recuperation du message et conversion en objet
                    var indice = JSON.parse(message.data);
                    //recuperation du cours de l'indice
                    var cours = indice.cours;

                    //si indice n'est pas en cache on l'ajoute
                    if(!that.alreadyExist(indice.nom)){
                        //initialisation du teableua des points de l'indice
                        indice.points = [];
                        indice.isVisible = true;

                        //on ajoute l'indice au cache
                        that.indicesCache.push(indice);

                        //on met a jour les donnes du graph
                        that.chart.options.data.push( {
                            type: "spline",
                            dataPoints: indice.points,
                            showInLegend: indice.isVisible,
                            visible: indice.isVisible,
                            name: indice.nom
                        })

                        that.showLegend(indice.nom);
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

    showLegend : function (nom) {
        //$('#legendContainer').append('<input type="checkbox">' + nom + '</input>');

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

            }
        );

        $('#selectValeurType').on('change', function () {

            alert("change: " + $(this).val());
        });

        // var $chkValBourse = $('.valBoursiereCheckBox');
        //
        // $chkValBourse.click(function (e) {
        //     //alert($(this).attr('name') + $(this).prop('checked'));
        //
        //     var indice = that.getObjectPositionInArray($(this).prop('name'));
        //
        //     if($(this).prop('checked')){
        //         indice.isVisible = true;
        //     }else{
        //         indice.isVisible = false;
        //     }
        //
        //     that.chart.render();
        //
        // });

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
        this.indicesCache = [];
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

        var nbreElements = this.indicesCache.length;
        console.log(1);

        if (nbreElements === 0) {
            return false;
        } else {

            for (cpt = 0; cpt < nbreElements; cpt++) {

                var cachingIndice = this.indicesCache[cpt];

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
        var nbreElements = this.indicesCache.length;

        for(cpt = 0; cpt < nbreElements; cpt ++){
            var cachingIndice = this.indicesCache[cpt];
            //console.log(cachingIndice);
            if(cachingIndice.nom === nom){
                return this.indicesCache[cpt];
            }
        }

        console.log("Error no object found");
    }

};

