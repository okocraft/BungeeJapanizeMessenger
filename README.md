# LunaChatBridge
LunaChatを導入しているBungeecordの子サーバー同士で連携させるプラグイン

## 使い方
* LunaChatプラグインをBukkit・Spigotサーバーのpluginsフォルダーに入れる
* LunaChatBridgeプラグインをBukkit・SpigotサーバーとBungeecord・waterfallサーバーのpluginsフォルダーに入れる
* ゲーム内にログインして、 `/lunachatbridge bungeechannel add <channel>` というコマンドをそれぞれのサーバーで実行する

## 注意
* LunaChatを入れておかないと起動しない
* チャンネルチャットを貫通させたいサーバーすべてで上記のaddコマンドを使う必要がある

## 特徴
* サーバー間でのLunaChatを利用したtell、replyコマンド
* サーバー間でのtell、replyはそれぞれのサーバーの `./plugins/LunaChat/logs` にログが記録される
* Bungeecordサーバー間の同名チャンネルのチャット貫通。これもログに記録される
* あとからBungeecord/Bukkitサーバーを起動しても機能し、先にBungeecordを起動しなくても良い
* プレフィックス、サフィックス、ワールド名などの情報を別サーバーに伝達
* BungeecordとBukkitに入れるjarは同じなので、わかりやすい
* japanizeするかどうかは送信**元**のサーバーの設定が適応される
* japanizeのフォーマットは送信**先**のサーバーの設定が適応される
* 発言するチャンネルの設定（フォーマットなど）も送信**先**のサーバーの設定が適応される

## 権限
* `lunachatbridge.*`
  * 説明: 全権限。
  * 子権限:
    * `lunachatbridge.bungeechannel.*`: true
    * `lunachatbridge.tell`: true
  * デフォルト権限: op

* `lunachatbridge.bungeechannel.*`
  * 説明: bungeechannelコマンドの全権限
  * 子権限:
    * `lunachatbridge.bungeechannel.add`: true
    * `lunachatbridge.bungeechannel.remove`: true
    * `lunachatbridge.bungeechannel.list`: true
  * デフォルト権限: op

* `lunachatbridge.bungeechannel.add`
  * 説明: `/lunachatbridge bungeechannel add <channel>` コマンドの権限
  * 子権限:
    * `lunachatbridge.bungeechannel.add.moderator`: true
  * デフォルト権限: op

* `lunachatbridge.bungeechannel.remove`
  * 説明: `/lunachatbridge bungeechannel remove <channel>` コマンドの権限
  * 子権限:
    * `lunachatbridge.bungeechannel.remove.moderator`: true
  * デフォルト権限: op

* `lunachatbridge.bungeechannel.list`
  * 説明: `/lunachatbridge bungeechannel list` コマンドの権限
  * デフォルト権限: op

* `lunachatbridge.bungeechannel.add.moderator`
  * 説明: 自分がモデレーターのチャンネルのみaddコマンドを利用可能になる権限
  * デフォルト権限: true

* `lunachatbridge.bungeechannel.remove.moderator`
  * 説明: 自分がモデレーターのチャンネルのみremoveコマンドを利用可能になる権限
  * デフォルト権限: true

* `lunachatbridge.tell`
  * 説明: サーバー間でtell、replyコマンドを貫通させるための権限。
  * デフォルト権限: true

## コマンド
* `/lunachatbridge <引数...>`

  このプラグインのベースコマンド。以下が短縮コマンドとして使える
  * `/lunachatbridge <引数...>`
  * `/lcbridge <引数...>`
  * `/lcb <引数...>`

* `/lunachatbridge bungeechannel add <channel>`

  `<channel>` を他のBungeecordの配下にあるLunaChatを導入したサーバーに公開する。これにより、他のサーバーの公開されている同名チャンネルの間でチャットが共有される。チャットのフォーマットは各自のサーバーのものが表示される。

* `/lunachatbridge bungeechannel remove \<channel>`

  `<channel>` の公開をやめる。他のサーバーの同名チャンネルのチャットが表示されなくなり、こちらのチャットも他のサーバーの同名チャンネルには表示されなくなる。

* `/lunachatbridge bungeechannel list`

  このサーバーが公開しているチャンネルの一覧を表示する。