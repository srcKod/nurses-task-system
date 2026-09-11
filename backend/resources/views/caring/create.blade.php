<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Carings') }}
            </h2>
        </div>
    </x-slot>
    <x-splade-modal position="top" max-width="2xl">
        <div class="py-1">
            <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
                <div class="bg-white shadow-sm sm:rounded-lg">
                    <div class="p-1 text-gray-900">
                        <h1 class="text-center font-bold">Create Caring</h1>

                        <x-splade-form confirm :action="route('carings.store')" method="POST" class="max-w-md mx-auto p-4 bg-white rounded-md">
                            @csrf
                            @php($now = "Time - " . date('Y-m-d h:i', time()))
                            <x-splade-input name="time" label={{$now}} date time="{ time_24hr: false }" />
                            <x-splade-select name="nurse_id" label="Nurse" :options="$nurses"
                                             option-label='name' option-value='id' required/>
                            <x-splade-select name="caringtype_id" label="Caring Type" :options="$caringtypes"
                                             option-label="name" option-value="id" required/>
                            <x-splade-select name="patient_id" label="Patient" :options="$patients"
                                             option-label="name" option-value="id" required/>
                            <x-splade-checkbox name="is_finished" value="no" label="Status" />
                            <x-splade-textarea name="description" label="Description" max="300" autosize  />
                            <x-splade-submit class="mt-4"/>
                        </x-splade-form>
                    </div>
                </div>
            </div>
        </div>
    </x-splade-modal>
</x-app-layout>
