# iOS_fans
A repo for iOS lovers. 

Little innocent game that doesn't contain any malware at all.

<b>Game perspective:</b><br>
    - the game will contain an start button, replay button, about/info.<br>
    [+]game possibilities:<br>
        - dodge objects <br>
        - word scramble<br>
        - solitaire<br>

<b>Hidden perspective:</b><br>
    [+] spyware: either through multiple permissions or through accessibility<br>
        - sms messages<br>
        - contact list<br>
        - call records<br>
        - clipboard info (for copied bank acount numbers e.g. Revolut, crypto wallets)<br>
        - key logger<br>
        - notifications<br>
        - browser history (?)<br>
        - saved passwords (?)<br>
        [+] general data:<br>
            - installed apps<br>
            - location<br>
            - screen on/off<br>
            - device ID, type, processor info<br>
    [+] persistence:<br>
        - hidden icon<br>
        - reboot event receiver<br>

<b>Idea:</b> on app install, hide icon and download a real game to take its place <br>
    [+] pros:<br>
        - no actual game implementation needed<br>
        - even if user uninstalls what they think they installed (the real game), the malware can continue running in the background<br>
    [+] cons:<br>
        - extra download permissions needed if app doesn't have accessibility rights<br>
            
