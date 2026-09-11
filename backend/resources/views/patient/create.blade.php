<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Patients') }}
            </h2>
        </div>
    </x-slot>
    <x-splade-modal position="top" max-width="2xl">
        <div class="py-10">
            <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
                <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                    <div class="p-1 text-gray-900">
                        <h1 class="text-center font-bold">Create Patient</h1>
                        <x-splade-form confirm :action="route('patients.store')" method="POST" class="max-w-md mx-auto p-4 bg-white rounded-md">
                            @csrf
                            <x-splade-input name="name" label="Name" max="50" required />
                            <x-splade-file name="room_photo_path" label="Room Photo" :show-filename="true" />
                            <img v-if="form.room_photo_path" :src="form.$fileAsUrl('room_photo_path')" />
                            <x-splade-select name="is_stopped" label="Status">
                                    <option value="1">Stopped</option>
                                    <option value="0">Not Stopped</option>
                            </x-splade-select>
                            <x-splade-submit class="mt-4"/>
                        </x-splade-form>
                    </div>
                </div>
            </div>
        </div>
    </x-splade-modal>
</x-app-layout>
