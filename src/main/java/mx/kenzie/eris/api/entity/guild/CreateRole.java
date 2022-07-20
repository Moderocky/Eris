package mx.kenzie.eris.api.entity.guild;

import mx.kenzie.argo.meta.Optional;
import mx.kenzie.eris.api.entity.Snowflake;
import mx.kenzie.eris.data.Payload;

public abstract class CreateRole extends Snowflake {
    public @Optional String name;
    public @Optional String permissions, unicode_emoji;
    public @Optional Integer color;
    public @Optional Boolean hoist, mentionable;
    public @Optional Payload icon;
    
    //    name	string	name of the role	"new role"
//    permissions	string	bitwise value of the enabled/disabled permissions	@everyone permissions in guild
//    color	integer	RGB color value	0
//    hoist	boolean	whether the role should be displayed separately in the sidebar	false
//    icon	?image data	the role's icon image (if the guild has the ROLE_ICONS feature)	null
//    unicode_emoji	?string	the role's unicode emoji as a standard emoji (if the guild has the ROLE_ICONS feature)	null
//    mentionable	boolean	whether the role should be mentionable	false
}
