<%@ taglib prefix="s" uri="http://stripes.sourceforge.net/stripes.tld" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title id="page-title">LaDaube</title>

    <script src="${pageContext.request.contextPath}/js/audiojs/audiojs/audio.js"></script>

    <!--  ext JS 3.0 -->
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="js/ext-3.0.3/resources/css/xtheme-gray.css"/>
    <link rel="stylesheet" type="text/css" href="css/ladaube.css"/>
    <script type="text/javascript" src="js/ext-3.0.3/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="js/ext-3.0.3/ext-all-debug.js"></script>
    <script type="text/javascript" src="js/ext-3.0.3/examples.js"></script>

    <link rel="stylesheet" type="text/css" href="js/livegrid/resources/css/ext-ux-livegrid.css" />
    <script type="text/javascript" src="js/livegrid/livegrid-all-debug.js"></script>

    <script type="text/javascript">

        audiojs.events.ready(function() {
            a = audiojs.createAll({
                trackEnded: function() {
                    if (window.player) {
                        window.player.notifySongOver();
                    }
                }
            });
        });

    </script>

    <script type="text/javascript">

        Ext.BLANK_IMAGE_URL = 'js/ext-3.0.3/resources/images/default/s.gif';
        Ext.onReady(function() {

            var username = "${actionBean.user.username}";

            var getPlayer = function() {
                return a[0];
            };

            var Player = Ext.extend(Ext.util.Observable, {

                playlist: [],
                current: -1,

                constructor: function(config) {
                    Player.superclass.constructor.call(config);
                    this.addEvents(['songStarted','songOver']);
                },

                play: function(playlist) {
                    this.current = -1;
                    this.playlist = playlist;
                    this.next();
                },

                next: function() {
                    if (this.current < this.playlist.length-1) {
                        this.current++;
                    }
                    var plItem = this.playlist[this.current];
                    var trackId = plItem.id;
                    var url = 'stream/' + trackId;

                    // play track
                    getPlayer().load(url);
                    getPlayer().play();
                    this.fireEvent('songStarted', plItem);

                    // update track details
                    Ext.Ajax.request({
                        url:'track',
                        params: {
                            track: trackId,
                            json: true
                        },
                        failure: function() { alert('error !'); },
                        success: function(response) {
                            var data = Ext.util.JSON.decode(response.responseText);
                            this.updateTrackDetails(data);
                        },
                        scope: this
                    });
                },

                notifySongOver: function() {
                    this.fireEvent('songOver');
                    this.next();
                },

                updateTrackDetails: function(track) {
                    var songName = Ext.get('song-name');
                    songName.update(track.name, false);
                    var albumName = Ext.get('album-name');
                    albumName.update(track.album, false);
                    var artistName = Ext.get('artist-name');
                    artistName.update(track.artist, false);
                    var url = "${pageContext.request.contextPath}/image/" + track.id;
                    var trackImage = Ext.get('track-image');
                    trackImage.dom.src = url;
                }
            });
            window.player = new Player({});

            Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
            
            Ext.QuickTips.init();

            var store = new Ext.ux.grid.livegrid.Store({
                autoLoad : true,
                url : '${pageContext.request.contextPath}/list?json=true',
                bufferSize : 1000,
                reader : new Ext.ux.grid.livegrid.JsonReader({
                    root : 'data',
                    totalProperty : 'totalCount'
                }, [
                    {name: 'sequence', sortType : 'int'}, // used for playlists only
                    {name: 'id'},
                    {name: 'name', sortType : 'string'},
                    {name: 'album', sortType : 'string'},
                    {name: 'artist', sortType : 'string'},
                    {name: 'year', sortType : 'int'},
                    {name: 'genre', sortType : 'string'},
                    {name: 'trackNumber', sortType : 'int'},
                    {name: 'userId', sortType : 'string'},
                    {name: 'postedOn', sortType : 'string'}
                ])
            });

            var searchField = new Ext.form.TextField({
                blankText: 'Filter...',
                enableKeyEvents: true,
                listeners: {
                    keydown: function(o, evt){
                        if (evt.keyCode==13) {
                            doFilter();
                        }
                    }
                }
            });

            var doFilter = function() {
                var crit = searchField.getValue();
                if (crit) {
                    store.setBaseParam('query', crit);
                } else {
                    store.setBaseParam('query', null);
                }
                store.load();
            };

            var bannerPanel = new Ext.Panel({
                height: 60,
                region: 'north',
                layout: 'border',
                border:false,
                items: [
                    {
                        region: 'center',
                        border:false,
                        contentEl: 'banner'
                    },
                    {
                        region: 'east',
                        width: 200,
                        border: false,
                        contentEl: 'loginBox'
                    }                               
                ]
            });

            var myView = new Ext.ux.grid.livegrid.GridView({
                nearLimit : 100,
                loadMask  : {
                    msg :  'Buffering...'
                }
            });

            var tracksGrid = new Ext.ux.grid.livegrid.GridPanel({
                region: 'center',
                enableDragDrop : true,
                ddGroup: 'gridDDGroup',
                stripeRows: true,
                stateId: 'tracksGrid',
                stateful: true,                
                cm             : new Ext.grid.ColumnModel([
                    new Ext.grid.RowNumberer({header : '#' }),
                    {header: 'Title', dataIndex: 'name', width: 200, sortable: true, menuDisabled: true},
                    {header: 'Album', dataIndex: 'album', width: 200, sortable: true, menuDisabled: true},
                    {header: 'Artist', dataIndex: 'artist', width: 200, sortable: true, menuDisabled: true},
                    {header: 'Year', dataIndex: 'year', width: 50, sortable: true, menuDisabled: true},
                    {header: 'Genre', dataIndex: 'genre', width: 50, sortable: true, menuDisabled: true},
                    {header: 'Posted by', dataIndex: 'userId', width: 70, sortable: false, menuDisabled: true},
                    {header: 'Posted on', dataIndex: 'postedOn', width: 70, sortable: true, menuDisabled: true}
                ]),
                loadMask       : {
                    msg : 'Loading tracks...'
                },
                title          : 'Tracks',
                store    : store,
                selModel : new Ext.ux.grid.livegrid.RowSelectionModel(),
                view     : myView,
                bbar     : new Ext.ux.grid.livegrid.Toolbar({
                    view        : myView,
                    displayInfo : true
                }),
                tbar: [
                    'Filter: ',
                    ' ',
                    searchField,
                    {
                        xtype: 'button',
                        text: 'filter',
                        listeners: {
                            click: function() {
                                // filter the store
                                doFilter();
                            }
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'clear',
                        listeners: {
                            click: function() {
                                searchField.setValue(null);
                                searchField.focus();
                                doFilter();
                                store.load();
                            }
                        }
                    }
                ],
                tools: [
                    {
                        id: 'search',
                        qtip: 'Filter tracks',
                        handler: function(event, toolEl, panel) {
                            var tbar = panel.getTopToolbar();
                            if (tbar.isVisible()) {
                                tbar.hide();
                            } else {
                                tbar.show();
                            }
                        }
                    }
                ]                
            });

            tracksGrid.on('rowclick', function(grid, index, e) {
                // build playlist (list of track Ids)
                var playlist = [];
                var recNum = store.bufferRange[0];
                store.each(function(rec) {
                    if (recNum >= index) {
                        playlist.push({id:rec.data.id, recNum:recNum});
                    }
                    recNum++;
                    return true;
                });
                // invoke player
                window.player.play(playlist);
            });

            window.player.on('songStarted', function(plItem) {
                var recNum = plItem.recNum;
                var selModel = tracksGrid.selModel;
                selModel.selectRow(recNum);
            });

            var buddiesStore = new Ext.data.JsonStore({
                url: '${pageContext.request.contextPath}/buddies?json=true',
                fields: [
                    {name: 'id'}
                ]
            });

            var buddiesGrid = new Ext.ListView({
                store: buddiesStore,
                emptyText: 'You have no buddies !',
                reserveScrollOffset: true,
                hideHeaders:true,
                border: false,
                columns: [
                    {
                        header: 'username',
                        dataIndex: 'id'
                    }
                ],
                listeners: {
                    click: function(dataView, index) {
                        // set mode to buddy
                        mode = 2;
                        var buddyId = buddiesStore.getAt(index).data.id;
                        store.setBaseParam('playlistId', null);
                        store.setBaseParam('buddyId', buddyId);
                        store.load();
                    }
                }
            });

            var playlistsStore = new Ext.data.JsonStore({
                url: '${pageContext.request.contextPath}/playlists?json=true',
                fields: [
                    {name: 'id'},
                    {name: 'name'},
                    {name: 'userId'}
                ]
            });

            var selectedPlaylistIndex = -1;
            var mode = 0; // 0==library, 1==playlist, 2==buddy (needed for popup on tracks grid)

            var playlistsGrid = new Ext.ListView({
                store: playlistsStore,
                emptyText: 'You have no playlists',
                reserveScrollOffset: true,
                hideHeaders:false,
                trackOver: true,
                columns: [
                    {
                        header: 'name',
                        dataIndex: 'name'
                    },
                    {
                        header: 'createdBy',
                        dataIndex: 'userId'
                    }
                ],
                listeners: {
                    click: function(dataView, index) {
                        var playlistId = playlistsStore.getAt(index).data.id;
                        store.setBaseParam('playlistId', playlistId);
                        store.setBaseParam('buddyId', null);
                        store.load();
                        // set mode to playlist
                        mode = 1;
                    },
                    mouseenter: function(dataView,index, node, e) {
                        selectedPlaylistIndex = index;
                    }
                }
            });

            var mainPanel = new Ext.Panel({
                layout: 'border',
                border: false,
                items: [
                    bannerPanel,
                    {
                        region: 'center',
                        layout: 'border',
                        border: false,
                        defaults: {
                            split: true
                        },
                        items : [
                            {
                                region: 'west',
                                width: 250,
                                minWidth: 150,
                                border: false,
                                split:true,
                                layout: 'border',
                                items: [
                                    {
                                        title: 'Home',
                                        border: false,
                                        region: 'north',
                                        iconCls: 'home-icon',
                                        contentEl: 'home'
                                    },
                                    {
                                        layout:'accordion',                                        
                                        region: 'center',
                                        items: [
                                            {
                                                title: 'Playlists',
                                                iconCls: 'playlists-icon',
                                                items : [playlistsGrid],
                                                id: 'plPanel',
                                                autoScroll: true,                                                
                                                hideCollapseTool: true,
                                                tools : [
                                                    {
                                                        id: 'plus',
                                                        qtip: 'Create new playlist',
                                                        handler: function(event, toolEl, panel) {
                                                            Ext.MessageBox.prompt('Name', 'Enter the name of the playlist:', function(btn, text) {
                                                                Ext.Ajax.request({
                                                                    url:'playlists',
                                                                    params: {
                                                                        createPlaylist: true,
                                                                        name: text,
                                                                        json: true
                                                                    },
                                                                    failure: function() { alert('error !'); },
                                                                    success: function(response) {
                                                                        playlistsStore.load();
                                                                    }
                                                                });
                                                            });
                                                        }
                                                    }
                                                ]
                                            },
                                            {
                                                title: 'Buddies',
                                                iconCls: 'buddies-icon',
                                                items : [buddiesGrid],
                                                autoScroll: true,                                                
                                                hideCollapseTool: true,
                                                tools : [
                                                    {
                                                        id: 'plus',
                                                        qtip: 'Invite buddy'
                                                    }
                                                ]
                                            },
                                            {
                                                title: 'Upload',
                                                autoScroll: true,
                                                iconCls: 'upload-icon',
                                                hideCollapseTool: true,
                                                contentEl: 'upload'
                                            }
                                        ]
                                    }
                                ]
                            },
                            tracksGrid
                        ]
                    }
                ]
            });

            new Ext.Viewport({
                layout:'fit',
                items:[mainPanel],
                renderTo: document.body
            });

            // register listener for styling the "library" item
            var libraryEm = Ext.get('library');
            libraryEm.on('mouseover', function() {
                libraryEm.addClass('x-list-over');
            });
            libraryEm.on('mouseout', function() {
                libraryEm.removeClass('x-list-over');
            });
            libraryEm.on('click', function() {
                displayAllTracks();
            }, this);

            var displayAllTracks = function() {
                store.setBaseParam('playlistId', null);
                store.setBaseParam('buddyId', null);
                store.load();
                // set mode to "library"
                mode = 0;
            };

            buddiesStore.load();
            playlistsStore.load();
            displayAllTracks();

        /****
        * Setup Drop Targets
        ***/

        // This will make sure we only drop to the view scroller element
        var dropTarget = new Ext.dd.DropTarget(playlistsGrid.el.dom, {
                ddGroup    : 'gridDDGroup',
                notifyDrop : function(ddSource, e, data){
                    var selectedRecords = ddSource.dragData.selections;
                    var params = {};
                    params.addTracks = true;
                    params.playlist = playlistsStore.getAt(selectedPlaylistIndex).data.id;
                    params.json = true;
                    var i;
                    for (i=0;i<selectedRecords.length;i++) {
                        params["tracks[" + i + "]"] = selectedRecords[i].data.id;
                    }
                    Ext.Ajax.request({
                        url:'playlists',
                        params: params,
                        failure: function() { alert('error !'); },
                        success: function(response) {
                            Ext.example.msg('Playlist updated', i + ' track(s) added to playlist.');
                        }
                    });
                }
         });


            /* context menu */
            var contextMenuPlaylists = new Ext.menu.Menu({
                id: 'contextMenuPlaylists',
                items: [
                    {
                        text: 'Delete',
                        handler: function() {
                            var params = {};
                            params['delete'] = true;
                            params.playlist = playlistsStore.getAt(selectedPlaylistIndex).data.id;
                            params.json = true;
                            Ext.Ajax.request({
                                url:'playlists',
                                params: params,
                                failure: function() { alert('error !'); },
                                success: function(response) {
                                    Ext.example.msg('Playlist deleted', 'The playlist has been deleted.');
                                    playlistsStore.load();
                                }
                            });

                        }
                    }
                ]
            });

            playlistsGrid.on('contextmenu', function(dataView, rowIndex, node, e) {
                e.stopEvent();
                var plUserId = playlistsStore.getAt(rowIndex).data.userId;
                if (plUserId===username) {
                    // only show menu for owned playlists
                    var coords = e.getXY();
                    contextMenuPlaylists.showAt([coords[0], coords[1]]);
                }
            });

            var contextMenuTracks = new Ext.menu.Menu({
                id: 'contextMenuTracks',
                items: [
                    {
                        text: 'Remove from playlist',
                        handler: function() {
                            var params = {};
                            params.removeTracks = true;
                            params.json = true;
                            params.playlist = playlistsStore.getAt(selectedPlaylistIndex).data.id;
                            // get selected records to remove
                            var selections = tracksGrid.getSelectionModel().getSelections();
                            if (selections.length > 0) {
                                var i;
                                for (i=0;i<selections.length;i++) {
                                    params["tracks[" + i + "]"] = selections[i].data.id;
                                }
                                Ext.Ajax.request({
                                    url:'playlists',
                                    params: params,
                                    failure: function() { alert('error !'); },
                                    success: function(response) {
                                        Ext.example.msg('Tracks removed', 'Removed ' + i + ' track(s).');
                                        store.setBaseParam('playlistId', params.playlist);
                                        store.load();
                                        // set mode to playlist
                                        mode = 1;
                                    }
                                });
                            }
                        }
                    }
                ]
            });            

            tracksGrid.on('rowcontextmenu', function(dataView, rowIndex, e) {
                // only on playlist view for now
                var selections = tracksGrid.getSelectionModel().getSelections();
                if (selections.length > 0) {
                    if (mode==1) {
                        e.stopEvent();
                        var coords = e.getXY();
                        contextMenuTracks.showAt([coords[0], coords[1]]);
                    }
                }
            });
            
        });
    </script>
</head>
<body>

<div id="home" class="x-panel-body">
    <div class="x-list-wrap">
        <div class="x-list-body">
            <div class="x-list-body-inner">
                <dl class=" ">
                    <dt style="width: 100%; text-align: left;"><em id="library" unselectable="on">Library</em></dt>
                    <div class="x-clear"></div>
                </dl>
            </div>
        </div>
    </div>
</div>

<div id="upload" class="x-hidden">
    <h1>Upload a file</h1>
    <p>
        Select an mp3 file on your computer
        and upload it to your account :
    </p>
    <s:form action="/upload" name="uploadForm">
        <s:file name="data" size="10" id="dataInput"/>
        <s:submit name="upload" id="uploadButton"/>
    </s:form>
    <br/>
    <h1>Use the mass import tool</h1>
    <p>
        You can upload several files at once using
        our <a href="${pageContext.request.contextPath}/webstart/ladaube-uploader.jnlp">mass import tool</a>.
    </p>
</div>

<div id="banner" class="x-hidden">
    <%@ include file="../logo.jsp"%>
    <div id="player">
        <img id="track-image" src="${pageContext.request.contextPath}/images/unknown.jpg" alt="track image"/>
        <div id="player-song-details">
            <audio></audio>
            <div id="song-details">
                <span id="song-name"></span>
                <span id="album-name"></span>
                <span id="artist-name"></span>
            </div>
        </div>
    </div>

    </div>
</div>
<div id="loginBox" class="x-hidden">
    <p>
        Logged in as <span class="loginName">${actionBean.user.username}</span>
        - <a href="${pageContext.request.contextPath}/login?logout=true">logout</a>
    </p>
</div>

</body>
</html>