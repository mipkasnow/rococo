<script lang="ts">
    import {onMount} from "svelte";
	import { clearSession } from "$lib/api/authUtils";
    import Loader from "$lib/components/Loader.svelte";

    onMount(async () => {
        clearSession();
        window.location.replace("/");
    });

</script>

<Loader/>
